package com.peanech.cryptoapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

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