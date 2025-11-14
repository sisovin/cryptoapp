package com.peanech.cryptoapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CoinDetailDao {
    @Query("SELECT * FROM coin_details WHERE id = :id")
    suspend fun get(id: String): CoinDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CoinDetailEntity)
}