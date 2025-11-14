# Project overview

You’re building a modern, native Android crypto app using Kotlin, Jetpack Compose, and Jetpack libraries. This document provides a complete, onboarding‑ready specification: architecture, setup, modules, dependencies, data models, state management (MVVM/MVI), UI flows, Firebase Auth + Realtime Database, CoinGecko API integration, Room caching/offline support, WorkManager background tasks, security, testing, CI/CD, and delivery checklists.

---

# Architecture

## High-level design

- **UI:** Jetpack Compose Material 3, Navigation Compose, Coil (images).
- **State management:** MVVM (ViewModel + Kotlin Coroutines + StateFlow) with unidirectional data flow; optional MVI reducer for deterministic updates.
- **Data:** Repositories calling CoinGecko REST API (Retrofit/OkHttp), Firebase Auth, Firebase Realtime Database, Room for local caching and offline.
- **DI:** Hilt for dependency injection.
- **Background work:** WorkManager for periodic sync and price alert checks.
- **Config & secrets:** google-services.json for Firebase; BuildConfig for API base URLs.

Data flow:
```
[Compose Screens] -> [ViewModel (MVVM/MVI)] -> [UseCases] -> [Repositories]
                                   |                 \
                               [StateFlow]          [Room Cache]
                                   |                 [Firebase Auth/RTDB]
                                   \---> [Retrofit -> CoinGecko API]
```

---

# Project setup

## Prerequisites

- Android Studio (Koala+), Windows 11 10.0 amd64.
- **```ps
------------------------------------------------------------
Gradle 9.1.0
------------------------------------------------------------

Build time:    2025-09-18 13:05:56 UTC
Revision:      e45a8dbf2470c2e2474ccc25be9f49331406a07e

Kotlin:        2.2.0
Groovy:        4.0.28
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  21.0.8 (Oracle Corporation 21.0.8+12-LTS-250)
Daemon JVM:    C:\Program Files\Java\jdk-21 (from org.gradle.java.home)
OS:            Windows 11 10.0 amd64
```**
- Firebase project created; **google-services.json** added to app module.
- Firebase Authentication enabled (Google, Email/Password).
- Firebase Realtime Database enabled (rules secure for authenticated users).
- CoinGecko public REST API (no API key).

## Modules

- **:app:** Android UI, navigation, DI wiring, resources.
- **:data:** Retrofit services, Room DAOs, repository implementations, Firebase data sources.
- **:domain:** Models and use cases (business logic).
- **:core-ui:** Shared Compose components, themes, typography, icons.
- **:testing:** Test utilities and fakes.

## Key dependencies

- **Compose:** material3, navigation-compose, activity-compose, coil-compose.
- **Lifecycle/State:** lifecycle-viewmodel-compose, lifecycle-runtime-ktx, coroutines, StateFlow.
- **Networking:** retrofit, okHttp, kotlinx-serialization or Moshi (choose one).
- **DI:** hilt-android, hilt-compiler.
- **Persistence:** room-runtime, room-ktx, room-compiler.
- **Firebase:** firebase-auth, firebase-database, firebase-analytics (optional).
- **Background:** work-runtime-ktx.
- **Testing:** junit, mockk, turbine, espresso, compose-ui-test-junit4.

Gradle hints:
- Enable Jetpack Compose, Kotlin compiler extension.
- Add Google Services plugin to app module.

---

# Environment and configuration

## Firebase configuration

- **google-services.json:** Place in app/src/debug and app/src/release.
- **Auth providers:**
  - Enable **Google**; add SHA-1/SHA-256 fingerprints; configure OAuth consent screen.
  - Enable **Email/Password** provider.
- **Realtime Database rules** (development; harden for production):
```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

## BuildConfig constants

- **COINGECKO_BASE_URL:** https://api.coingecko.com/api/v3
- **DEFAULT_VS_CURRENCY:** "usd"
- **MARKETS_PAGE_SIZE:** 50
- **CACHE_TTL_MARKETS_MS:** 60_000
- **CACHE_TTL_DETAIL_MS:** 300_000

---

# Data layer

## CoinGecko endpoints

- **Markets:** /coins/markets?vs_currency={currency}&order=market_cap_desc&per_page={n}&page={p}&sparkline=false&price_change_percentage=1h,24h,7d
- **Coin detail:** /coins/{id}?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=true

## Domain models

```kotlin
data class MarketCoin(
  val id: String,
  val symbol: String,
  val name: String,
  val imageUrl: String,
  val currentPrice: Double,
  val marketCap: Double?,
  val change24hPct: Double?,
  val high24h: Double?,
  val low24h: Double?
)

data class CoinDetail(
  val id: String,
  val symbol: String,
  val name: String,
  val imageSmall: String,
  val currentPrice: Double,
  val change24hPct: Double?,
  val high24h: Double?,
  val low24h: Double?,
  val sparkline7d: List<Double>
)
```

## Room entities and DAOs

```kotlin
@Entity(tableName = "market_coins")
data class MarketCoinEntity(
  @PrimaryKey val id: String,
  val symbol: String,
  val name: String,
  val imageUrl: String,
  val currentPrice: Double,
  val marketCap: Double?,
  val change24hPct: Double?,
  val high24h: Double?,
  val low24h: Double?,
  val updatedAt: Long
)

@Dao
interface MarketCoinDao {
  @Query("SELECT * FROM market_coins ORDER BY marketCap DESC LIMIT :limit OFFSET :offset")
  suspend fun getPaged(limit: Int, offset: Int): List<MarketCoinEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<MarketCoinEntity>)
}

@Entity(tableName = "coin_details")
data class CoinDetailEntity(
  @PrimaryKey val id: String,
  val symbol: String,
  val name: String,
  val imageSmall: String,
  val currentPrice: Double,
  val change24hPct: Double?,
  val high24h: Double?,
  val low24h: Double?,
  val sparkline7d: String, // JSON string of doubles
  val updatedAt: Long
)

@Dao
interface CoinDetailDao {
  @Query("SELECT * FROM coin_details WHERE id = :id")
  suspend fun get(id: String): CoinDetailEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: CoinDetailEntity)
}
```

## Firebase data structure

- **users/{uid}/profile:** displayName, photoUrl, email.
- **users/{uid}/preferences:** currency ("usd"), theme ("system").
- **users/{uid}/watchlist:** map of coinId -> true.
- **users/{uid}/alerts:** coinId -> threshold objects (optional).

## Repository interfaces

```kotlin
interface MarketRepository {
  suspend fun getMarkets(page: Int, perPage: Int, vsCurrency: String): List<MarketCoin>
  suspend fun refreshMarkets(page: Int, perPage: Int, vsCurrency: String): List<MarketCoin>
}

interface CoinRepository {
  suspend fun getCoinDetail(id: String, vsCurrency: String): CoinDetail
  suspend fun refreshCoinDetail(id: String, vsCurrency: String): CoinDetail
}

interface UserRepository {
  suspend fun currentUser(): FirebaseUser?
  suspend fun signInWithGoogle(idToken: String): FirebaseUser
  suspend fun signInWithEmail(email: String, password: String): FirebaseUser
  suspend fun signOut()
  suspend fun getWatchlist(uid: String): Set<String>
  suspend fun addToWatchlist(uid: String, coinId: String)
  suspend fun removeFromWatchlist(uid: String, coinId: String)
  suspend fun getPreferences(uid: String): UserPreferences
  suspend fun setPreferences(uid: String, prefs: UserPreferences)
}

data class UserPreferences(val currency: String = "usd", val theme: String = "system")
```

## Retrofit service

```kotlin
interface CoinGeckoService {
  @GET("coins/markets")
  suspend fun getMarkets(
    @Query("vs_currency") vsCurrency: String,
    @Query("order") order: String = "market_cap_desc",
    @Query("per_page") perPage: Int,
    @Query("page") page: Int,
    @Query("sparkline") sparkline: Boolean = false,
    @Query("price_change_percentage") pct: String = "1h,24h,7d"
  ): List<MarketCoinDto>

  @GET("coins/{id}")
  suspend fun getCoinDetail(
    @Path("id") id: String,
    @Query("localization") localization: Boolean = false,
    @Query("tickers") tickers: Boolean = false,
    @Query("market_data") marketData: Boolean = true,
    @Query("community_data") communityData: Boolean = false,
    @Query("developer_data") developerData: Boolean = false,
    @Query("sparkline") sparkline: Boolean = true
  ): CoinDetailDto
}
```

---

# State management

## MVVM with unidirectional data

- **ViewModel:** Holds immutable UI state in StateFlow, exposes intent handlers.
- **UI State:** Single source of truth per screen (Loading/Data/Error).
- **Events:** User actions (search, paginate, toggle watchlist).
- **Effects:** One-off events (toasts, navigation).

### Dashboard state

```kotlin
data class DashboardState(
  val isLoading: Boolean = false,
  val coins: List<MarketCoin> = emptyList(),
  val page: Int = 1,
  val query: String = "",
  val sort: SortOption = SortOption.MarketCapDesc,
  val watchlist: Set<String> = emptySet(),
  val error: String? = null,
  val currency: String = "usd"
)

enum class SortOption { MarketCapDesc, PriceDesc, Change24hDesc }
```

### Dashboard ViewModel

```kotlin
@HiltViewModel
class DashboardViewModel @Inject constructor(
  private val marketRepo: MarketRepository,
  private val userRepo: UserRepository
) : ViewModel() {

  private val _state = MutableStateFlow(DashboardState(isLoading = true))
  val state: StateFlow<DashboardState> = _state

  init { loadInitial() }

  fun onSearch(query: String) {
    _state.update { it.copy(query = query) }
    // local filter to avoid spamming API
  }

  fun onPaginate() {
    val nextPage = _state.value.page + 1
    viewModelScope.launch { fetchPage(nextPage, append = true) }
  }

  fun onRefresh() = viewModelScope.launch { fetchPage(1, append = false) }

  fun onToggleWatch(coinId: String) = viewModelScope.launch {
    val uid = userRepo.currentUser()?.uid ?: return@launch
    val watchlist = userRepo.getWatchlist(uid)
    if (coinId in watchlist) userRepo.removeFromWatchlist(uid, coinId)
    else userRepo.addToWatchlist(uid, coinId)
    _state.update { it.copy(watchlist = userRepo.getWatchlist(uid)) }
  }

  private fun loadInitial() = viewModelScope.launch {
    val uid = userRepo.currentUser()?.uid
    val watch = if (uid != null) userRepo.getWatchlist(uid) else emptySet()
    _state.update { it.copy(watchlist = watch) }
    fetchPage(1, append = false)
  }

  private suspend fun fetchPage(page: Int, append: Boolean) {
    _state.update { it.copy(isLoading = true, error = null) }
    runCatching { marketRepo.getMarkets(page, BuildConfig.MARKETS_PAGE_SIZE, _state.value.currency) }
      .onSuccess { data ->
        _state.update {
          it.copy(
            isLoading = false,
            page = page,
            coins = if (append) it.coins + data else data
          )
        }
      }
      .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
  }
}
```

---

# UI specification

## Navigation

- **Start:** Splash → Auth (Google, Email/Password) → Dashboard.
- **Main:** Dashboard → Coin Detail → Back → Dashboard.
- **Settings:** Dashboard top bar → Settings (Theme, Currency, Sign out).
- **Watchlist:** Tab or filter on Dashboard.

## Dashboard frontend

- **Top bar:** App name, search box, currency chip, settings icon.
- **List item:** Image, name + symbol, current price, 24h change (color-coded), market cap, watchlist toggle.
- **Behaviors:** Pull-to-refresh, infinite scroll, local search filter, sort options.

Compose sketch:
```kotlin
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel(), onOpenSettings: () -> Unit, onOpenDetail: (String) -> Unit) {
  val state by viewModel.state.collectAsState()
  Scaffold(
    topBar = { DashboardTopBar(
      query = state.query,
      onQueryChange = viewModel::onSearch,
      currency = state.currency,
      onSettingsClick = onOpenSettings
    ) }
  ) { padding ->
    when {
      state.isLoading && state.coins.isEmpty() -> Loading()
      state.error != null -> ErrorView(state.error!!) { viewModel.onRefresh() }
      else -> CoinList(
        coins = state.coins,
        watchlist = state.watchlist,
        onEndReached = viewModel::onPaginate,
        onCoinClick = onOpenDetail,
        onWatchToggle = viewModel::onToggleWatch,
        modifier = Modifier.padding(padding)
      )
    }
  }
}
```

## Dashboard backend

- **Fetch markets:** Retrofit -> CoinGecko markets.
- **Cache:** Save to Room with timestamp; use cache-first when within TTL; network refresh when stale.
- **Pagination:** Server-side via page/per_page; client merges pages.
- **Watchlist:** Read/write under /users/{uid}/watchlist in Firebase RTDB.

## Crypto detail frontend

- **Header:** Image, name, symbol, price, 24h change (green/red).
- **Stats:** 24h high/low, market cap (if available), volume (optional).
- **Sparkline:** 7d line chart (Compose Canvas or chart lib).
- **Actions:** Add/remove watchlist, share.

## Crypto detail backend

- **Fetch detail:** Retrofit -> CoinGecko /coins/{id}.
- **Cache:** Room with 5‑minute TTL; serve cached if offline.
- **Formatting:** Currency-aware with Android locale and selected currency.

## Settings frontend

- **Theme:** System/Light/Dark.
- **Currency:** USD/EUR/GBP/JPY (expand as needed).
- **Account:** Profile info, Sign out.
- **Data controls:** Clear local cache, export watchlist JSON.

## Settings backend

- **Preferences:** Persist currency/theme in Firebase RTDB and mirror in local DataStore (optional) for immediate UI application.
- **Sign out:** FirebaseAuth.signOut(), clear sensitive cache.

---

# Background processing with WorkManager

## Use cases

- **Periodic market refresh:** Sync top N coins hourly to keep cache warm.
- **Price alerts (optional):** Evaluate user-defined thresholds and post local notifications.

## Workers

```kotlin
class MarketsSyncWorker(
  appContext: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

  @Inject lateinit var marketRepo: MarketRepository

  override suspend fun doWork(): Result = runCatching {
    marketRepo.refreshMarkets(page = 1, perPage = BuildConfig.MARKETS_PAGE_SIZE, vsCurrency = "usd")
    Result.success()
  }.getOrElse { Result.retry() }
}
```

Scheduling:
```kotlin
val request = PeriodicWorkRequestBuilder<MarketsSyncWorker>(1, TimeUnit.HOURS)
  .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
  .build()
WorkManager.getInstance(context).enqueueUniquePeriodicWork("MarketsSync", ExistingPeriodicWorkPolicy.UPDATE, request)
```

---

# Security, privacy, and reliability

- **Authenticated DB access:** Restrict RTDB reads/writes to authenticated users.
- **No secrets:** CoinGecko requires no API keys; avoid embedding sensitive tokens.
- **Rate limiting:** Debounce user search; prefetch pages responsibly; cache to reduce calls.
- **Error handling:** Graceful messages for network errors; retries with backoff.
- **Offline support:** Serve Room cache; indicate stale data; allow manual retry.
- **Data integrity:** Validate numeric ranges; handle nulls from API safely.

---

# Performance and UX

- **Lists:** LazyColumn with stable item keys; Coil image caching and placeholders; shimmer for loading.
- **Recomposition:** Use derived state, remember, and avoid passing large lists through multiple layers unnecessarily.
- **Pagination:** Trigger on ~80% scroll; prevent concurrent loads.
- **Theming:** Material 3 dynamic color; accessible contrast for up/down badges.
- **Localization:** Resource-based strings; locale-aware currency formatting.
- **Accessibility:** Content descriptions; keyboard navigation; scalable text.

---

# Testing strategy

## Unit tests

- **Repositories:** Mock Retrofit and Room; verify cache TTL and mapping.
- **Use cases:** Sorting, filtering, watchlist operations.
- **ViewModels:** Event-to-state transitions; error and pagination handling.

## UI tests

- **Compose:** Render lists, loading, error states; interactions (search, paginate, toggle watchlist).
- **Auth flow:** Sign-in redirect logic; unauthenticated restrictions.

## Integration tests

- **WorkManager:** Periodic sync runs and updates Room.
- **Firebase RTDB (emulator):** Rules and read/write flows.
- **Offline mode:** Network down -> serve cache.

---

# DI and app wiring

## Hilt setup

- **App class:** Annotate with @HiltAndroidApp.
- **Modules:** Provide Retrofit, OkHttp, Room, DAOs, Repositories, Firebase, WorkManager.

Example:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides @Singleton
  fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder().build()

  @Provides @Singleton
  fun provideRetrofit(client: OkHttpClient): Retrofit =
    Retrofit.Builder()
      .baseUrl(BuildConfig.COINGECKO_BASE_URL)
      .addConverterFactory(MoshiConverterFactory.create())
      .client(client)
      .build()

  @Provides @Singleton
  fun provideService(retrofit: Retrofit): CoinGeckoService = retrofit.create(CoinGeckoService::class.java)

  @Provides @Singleton
  fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
    Room.databaseBuilder(ctx, AppDatabase::class.java, "crypto.db").build()

  @Provides fun provideMarketDao(db: AppDatabase): MarketCoinDao = db.marketCoinDao()
  @Provides fun provideDetailDao(db: AppDatabase): CoinDetailDao = db.coinDetailDao()

  @Provides @Singleton
  fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

  @Provides @Singleton
  fun provideRealtimeDb(): FirebaseDatabase = FirebaseDatabase.getInstance()
}
```

---

# Implementation highlights

## Mapping DTOs to domain and Room

- **DTO -> Domain:** Keep UI free of API field names; map nulls safely.
- **Domain -> Room Entity:** Include updatedAt timestamps to manage TTL.
- **Sparkline:** Store as JSON string; parse to List<Double> for chart.

## Currency switching

- **Source of truth:** User preference currency in RTDB mirrored locally.
- **Effect:** Re-fetch markets/detail with new vs_currency; re-render UI.

## Watchlist UX

- **Optimistic toggle:** Update UI immediately; reconcile on RTDB write failure.
- **Filter tab:** Quick view of watchlisted coins.

---

# Delivery checklist

- **Compose UI implemented:** Dashboard, Detail, Settings.
- **Auth configured:** Google and Email/Password functional.
- **RTDB rules:** Authenticated read/write enforced.
- **CoinGecko integration:** Markets and detail endpoints wired.
- **Room caching:** TTL respected; offline serving works.
- **WorkManager:** Periodic sync scheduled; optional alerts.
- **DI with Hilt:** All components provided and scoped correctly.
- **Error states/UI:** Loading, empty, error views present.
- **Accessibility & i18n:** Content descriptions; resource strings.
- **Testing:** Unit, UI, and integration tests passing.
- **CI/CD:** GitHub Actions build, test, lint; Firebase App Distribution for testers.
- **Release notes:** Changelog, known issues, telemetry (optional analytics).

---

# Advantages recap

- **Full Android API access:** Hardware integration, notifications, WorkManager reliability.
- **Performance & responsiveness:** Compose + Kotlin Coroutines + Room delivering fast UX.
- **Maintainability & scalability:** MVVM/MVI, modular layers, DI with Hilt.
- **Community support:** Jetpack libraries, Firebase, Retrofit, Room, WorkManager—rich docs and examples.

---

# Next steps

- Confirm charting approach (Compose Canvas vs third‑party).
- Decide on analytics (Firebase Analytics) and crash reporting (Crashlytics).
- Provide annotated architecture and data‑flow diagrams and an onboarding deck for your team. If you want, I’ll draft those diagrams and a slide-ready checklist tailored to your workflow.