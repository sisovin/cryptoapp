# CryptoApp

A modern, native Android cryptocurrency tracking app built with Kotlin, Jetpack Compose, and Firebase. This app allows users to browse cryptocurrency markets, view detailed coin information, manage a watchlist, and receive price alerts.

## Features

- **Market Overview**: Browse top cryptocurrencies with real-time price data, market cap, and 24h price changes
- **Coin Details**: View detailed information including price charts, market statistics, and historical data
- **Watchlist**: Add/remove coins to/from your personal watchlist stored in Firebase
- **Authentication**: Secure sign-in with Google or Email/Password using Firebase Auth
- **Offline Support**: Cache data locally with Room for offline viewing
- **Background Sync**: Periodic market data refresh using WorkManager
- **Modern UI**: Built with Jetpack Compose and Material 3 design
- **Search & Filter**: Search coins by name and filter your watchlist

## Architecture

The app follows a clean architecture pattern with MVVM (Model-View-ViewModel) and unidirectional data flow:

```
[Compose UI] → [ViewModel] → [Use Cases] → [Repositories]
                              ↓
                        [StateFlow]
                              ↓
                 [Room Cache] ↔ [Firebase RTDB]
                              ↓
                    [Retrofit → CoinGecko API]
```

### Modules

- **:app**: Main application module with UI and navigation
- **:data**: Data layer with repositories, Room database, and API services
- **:domain**: Business logic models and repository interfaces
- **:core-ui**: Shared UI components and theming
- **:testing**: Test utilities and shared test code

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with StateFlow
- **Dependency Injection**: Hilt
- **Networking**: Retrofit with OkHttp and Moshi
- **Local Database**: Room
- **Remote Database**: Firebase Realtime Database
- **Authentication**: Firebase Auth
- **Background Tasks**: WorkManager
- **Image Loading**: Coil
- **Build Tool**: Gradle with Kotlin DSL
- **Testing**: JUnit, MockK, Turbine

## Prerequisites

- Android Studio Koala (2024.1.1) or later
- JDK 21
- Android SDK API 35
- Firebase project with Authentication and Realtime Database enabled
- CoinGecko API (free, no API key required)

## Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/sisovin/cryptoapp.git
   cd cryptoapp
   ```

2. **Firebase Configuration**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Enable Authentication with Google and Email/Password providers
   - Enable Realtime Database
   - Download `google-services.json` and place it in `app/src/main/`
   - Configure Firebase Realtime Database rules:
     ```json
     {
       "rules": {
         ".read": "auth != null",
         ".write": "auth != null"
       }
     }
     ```

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   - Open in Android Studio
   - Run on device or emulator

## API Integration

The app integrates with the CoinGecko API for cryptocurrency data:

- **Markets Endpoint**: `/api/v3/coins/markets` - Get market data for coins
- **Coin Details Endpoint**: `/api/v3/coins/{id}` - Get detailed coin information

No API key is required for basic usage. Rate limits apply.

## Data Models

### MarketCoin
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
```

### CoinDetail
```kotlin
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

## State Management

The app uses MVVM with StateFlow for reactive state management:

```kotlin
data class DashboardState(
    val isLoading: Boolean = false,
    val coins: List<MarketCoin> = emptyList(),
    val page: Int = 1,
    val query: String = "",
    val watchlist: Set<String> = emptySet(),
    val currency: String = "usd",
    val error: String? = null
)
```

## Caching Strategy

- **Markets Data**: Cached for 1 minute in Room
- **Coin Details**: Cached for 5 minutes in Room
- **Watchlist**: Stored in Firebase Realtime Database
- **User Preferences**: Stored in Firebase

## Background Processing

WorkManager handles periodic market data synchronization:

```kotlin
val request = PeriodicWorkRequestBuilder<MarketsSyncWorker>(1, TimeUnit.HOURS)
    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
    .build()
```

## Testing

### Unit Tests
```bash
./gradlew test
```

### UI Tests
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
- Repository implementations
- ViewModel state transitions
- API data mapping
- UI component interactions

## Security

- Firebase Authentication for user management
- Encrypted data storage with Room
- Secure API communication with HTTPS
- No sensitive data stored locally

## Performance

- Lazy loading with Compose LazyColumn
- Image caching with Coil
- Efficient state updates with derivedStateOf
- Background data synchronization
- Offline data availability

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Code Style

The project follows Kotlin coding conventions and uses:
- ktlint for code formatting
- Detekt for static analysis
- Android Lint for Android-specific checks

## Deployment

### Google Play Store
1. Generate signed APK/AAB
2. Create Play Store listing
3. Upload and publish

### Firebase App Distribution
```bash
./gradlew assembleRelease appDistributionUploadRelease
```

## Known Issues

- Sparkline charts are basic (can be enhanced with charting libraries)
- Limited currency support (USD only in current implementation)
- No push notifications for price alerts

## Future Enhancements

- [ ] Advanced charting with MPAndroidChart or Compose charts
- [ ] Multiple currency support
- [ ] Price alerts with notifications
- [ ] Portfolio tracking
- [ ] News integration
- [ ] Dark mode toggle
- [ ] Biometric authentication

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [CoinGecko API](https://www.coingecko.com/en/api) for cryptocurrency data
- [Firebase](https://firebase.google.com/) for backend services
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI
- [Material Design 3](https://m3.material.io/) for design system

## Screenshots

*Add screenshots here*

## Support

For support, email support@cryptoapp.com or create an issue in this repository.

---

Built with ❤️ using modern Android development practices.
│   ├── shared/                 # KMP shared module
│   ├── core-ui/                # Reusable UI components
│   └── core-testing/           # Test utilities
├── ios/                        # iOS application (future)
└── docs/                       # Documentation
```

### Architecture Pattern

The app follows Clean Architecture principles with MVI (Model-View-Intent) pattern:

- **Presentation Layer**: ViewModels, Compose UI
- **Domain Layer**: Use cases, business logic
- **Data Layer**: Repositories, API services, local database

## Getting Started

### Prerequisites

- **Android Studio**: Koala (2024.1.1) or newer
- **Kotlin**: 1.9.0+
- **Java**: 17 (recommended)
- **Gradle**: 8.10.2+
- **Firebase Project**: With Authentication and Realtime Database enabled

### Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/sisovin/crypto-app.git
   cd crypto-app/android
   ```

2. **Firebase Configuration**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Enable Google Authentication and Realtime Database
   - Download `google-services.json` and place it in:
     - `app/src/debug/`
     - `app/src/release/`

3. **Build the project**

   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**

   ```bash
   ./gradlew installDebug
   ```

### Firebase Rules

Set up the following Realtime Database rules for development:

```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

## API Integration

### CoinGecko API

The app integrates with CoinGecko's public API for market data:

- **Markets Endpoint**: `/api/v3/coins/markets`
- **Coin Details**: `/api/v3/coins/{id}`
- **Rate Limits**: Respects API limits with caching and debouncing

### Firebase Services

- **Authentication**: Google Sign-In and email/password
- **Realtime Database**: User watchlists and preferences
- **Analytics**: User interaction tracking (optional)

## Development

### Building Modules

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :shared:build
./gradlew :app:build

# Run tests
./gradlew test
./gradlew :app:connectedAndroidTest
```

### Code Style

The project follows Kotlin coding conventions and uses:

- Detekt for static analysis
- Ktlint for code formatting
- Spotless for consistent formatting

### Testing Strategy

- **Unit Tests**: Business logic, ViewModels, repositories
- **Integration Tests**: API calls, database operations
- **UI Tests**: Compose component testing
- **End-to-End Tests**: Full user flows

## Deployment

### Android

1. **Generate signed APK/AAB**

   ```bash
   ./gradlew assembleRelease
   ./gradlew bundleRelease
   ```

2. **Firebase App Distribution**

   ```bash
   ./gradlew appDistributionUploadRelease
   ```

### CI/CD

GitHub Actions workflow includes:

- Automated testing
- Code quality checks
- Release builds
- Firebase distribution

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Review Process

- All PRs require review
- Tests must pass
- Code coverage maintained
- Follows established patterns

## Security

- **API Keys**: No sensitive keys stored in code
- **Authentication**: Firebase Auth for secure user sessions
- **Data Encryption**: Local database encrypted
- **Network Security**: HTTPS-only communications

## Performance

- **Lazy Loading**: Efficient list rendering with LazyColumn
- **Image Caching**: Coil for optimized image loading
- **Database Optimization**: SQLDelight with proper indexing
- **Memory Management**: Proper ViewModel scoping

## Known Issues & Limitations

- iOS support: Currently Android-only, iOS implementation planned
- Offline sync: Basic caching implemented, full sync pending
- Push notifications: Not implemented yet

## Future Enhancements

- **iOS App**: Complete SwiftUI implementation
- **Price Alerts**: Custom price notification system
- **Portfolio Tracking**: Investment portfolio management
- **News Integration**: Cryptocurrency news feeds
- **Advanced Charts**: Interactive price charts with indicators

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [CoinGecko API](https://www.coingecko.com/en/api) for market data
- [Firebase](https://firebase.google.com/) for backend services
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for UI framework
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) for cross-platform development

## Support

For support, email <support@cryptoapp.com> or join our Discord community.

---

**Built with ❤️ using Kotlin Multiplatform**
