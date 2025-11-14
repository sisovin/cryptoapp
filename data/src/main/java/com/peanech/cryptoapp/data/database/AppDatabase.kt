package com.peanech.cryptoapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MarketCoinEntity::class, CoinDetailEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun marketCoinDao(): MarketCoinDao
    abstract fun coinDetailDao(): CoinDetailDao
}