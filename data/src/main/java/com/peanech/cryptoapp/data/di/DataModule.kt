package com.peanech.cryptoapp.data.di

import android.content.Context
import androidx.room.Room
import com.peanech.cryptoapp.data.api.CoinGeckoService
import com.peanech.cryptoapp.data.database.AppDatabase
import com.peanech.cryptoapp.data.database.CoinDetailDao
import com.peanech.cryptoapp.data.database.MarketCoinDao
import com.peanech.cryptoapp.data.repository.CoinRepositoryImpl
import com.peanech.cryptoapp.data.repository.MarketRepositoryImpl
import com.peanech.cryptoapp.data.repository.UserRepositoryImpl
import com.peanech.cryptoapp.domain.CoinRepository
import com.peanech.cryptoapp.domain.MarketRepository
import com.peanech.cryptoapp.domain.UserRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideCoinGeckoService(retrofit: Retrofit): CoinGeckoService = retrofit.create(CoinGeckoService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context, AppDatabase::class.java, "crypto.db"
    ).build()

    @Provides
    fun provideMarketCoinDao(db: AppDatabase): MarketCoinDao = db.marketCoinDao()

    @Provides
    fun provideCoinDetailDao(db: AppDatabase): CoinDetailDao = db.coinDetailDao()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideMarketRepository(api: CoinGeckoService, dao: MarketCoinDao): MarketRepository = MarketRepositoryImpl(api, dao)

    @Provides
    @Singleton
    fun provideCoinRepository(api: CoinGeckoService, dao: CoinDetailDao): CoinRepository = CoinRepositoryImpl(api, dao)

    @Provides
    @Singleton
    fun provideUserRepository(auth: FirebaseAuth, db: FirebaseDatabase): UserRepository = UserRepositoryImpl(auth, db)
}