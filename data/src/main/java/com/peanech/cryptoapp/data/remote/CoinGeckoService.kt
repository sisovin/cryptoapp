package com.peanech.cryptoapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoService {
    @GET("coins/markets")
    suspend fun getMarkets(
        @Query("vs_currency") vsCurrency: String,
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("sparkline") sparkline: Boolean = false,
        @Query("price_change_percentage") pct: String = "1h,24h,7d"
    ): List<MarketCoinDto>

    @GET("coins/{id}")
    suspend fun getCoinDetail(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("sparkline") sparkline: Boolean = true
    ): CoinDetailDto
}