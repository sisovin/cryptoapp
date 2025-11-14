package com.peanech.cryptoapp.domain

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