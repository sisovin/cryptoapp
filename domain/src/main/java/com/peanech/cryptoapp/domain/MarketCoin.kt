package com.peanech.cryptoapp.domain

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