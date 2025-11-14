package com.peanech.cryptoapp.domain.repository

import com.peanech.cryptoapp.domain.CoinDetail

interface CoinRepository {
    suspend fun getCoinDetail(id: String, vsCurrency: String): CoinDetail
    suspend fun refreshCoinDetail(id: String, vsCurrency: String): CoinDetail
}