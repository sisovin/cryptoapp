package com.peanech.cryptoapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MarketCoinDao {
    @Query("SELECT * FROM market_coins ORDER BY marketCap DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int): List<MarketCoinEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MarketCoinEntity>)

    @Query("DELETE FROM market_coins WHERE updatedAt < :timestamp")
    suspend fun deleteExpired(timestamp: Long)
}

@Dao
interface CoinDetailDao {
    @Query("SELECT * FROM coin_details WHERE id = :id")
    suspend fun get(id: String): CoinDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CoinDetailEntity)

    @Query("DELETE FROM coin_details WHERE updatedAt < :timestamp")
    suspend fun deleteExpired(timestamp: Long)
}