package com.peanech.cryptoapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

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