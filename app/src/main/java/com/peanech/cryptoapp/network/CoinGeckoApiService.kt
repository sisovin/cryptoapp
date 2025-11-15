package com.peanech.cryptoapp.network

import com.peanech.cryptoapp.domain.MarketCoin
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApiService {

    @GET("coins/markets")
    suspend fun getMarkets(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<CoinGeckoMarketCoin>
}

data class CoinGeckoMarketCoin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val market_cap: Long?,
    val market_cap_rank: Int?,
    val price_change_percentage_24h: Double?,
    val high_24h: Double?,
    val low_24h: Double?
)

fun CoinGeckoMarketCoin.toMarketCoin(): MarketCoin {
    return MarketCoin(
        id = id,
        symbol = symbol,
        name = name,
        imageUrl = image,
        currentPrice = current_price,
        marketCap = market_cap?.toDouble(),
        change24hPct = price_change_percentage_24h,
        high24h = high_24h,
        low24h = low_24h
    )
}