package com.peanech.cryptoapp.domain.repository

import com.peanech.cryptoapp.domain.MarketCoin

interface MarketRepository {
    suspend fun getMarkets(page: Int, perPage: Int, vsCurrency: String): List<MarketCoin>
    suspend fun refreshMarkets(page: Int, perPage: Int, vsCurrency: String): List<MarketCoin>
}