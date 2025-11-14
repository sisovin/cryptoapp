package com.peanech.cryptoapp.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinDetailDto(
    @Json(name = "id") val id: String,
    @Json(name = "symbol") val symbol: String,
    @Json(name = "name") val name: String,
    @Json(name = "image") val image: ImageDto,
    @Json(name = "market_data") val marketData: MarketDataDto
)

@JsonClass(generateAdapter = true)
data class ImageDto(
    @Json(name = "small") val small: String
)

@JsonClass(generateAdapter = true)
data class MarketDataDto(
    @Json(name = "current_price") val currentPrice: Map<String, Double>,
    @Json(name = "price_change_percentage_24h") val change24hPct: Double?,
    @Json(name = "high_24h") val high24h: Map<String, Double>?,
    @Json(name = "low_24h") val low24h: Map<String, Double>?,
    @Json(name = "sparkline_7d") val sparkline7d: SparklineDto
)

@JsonClass(generateAdapter = true)
data class SparklineDto(
    @Json(name = "price") val price: List<Double>
)