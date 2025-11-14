package com.peanech.cryptoapp.data.repository

import com.peanech.cryptoapp.data.api.CoinGeckoService
import com.peanech.cryptoapp.data.database.MarketCoinDao
import com.peanech.cryptoapp.domain.MarketCoin
import com.peanech.cryptoapp.domain.MarketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val api: CoinGeckoService,
    private val dao: MarketCoinDao
) : MarketRepository {

    override suspend fun getMarkets(page: Int, perPage: Int, vsCurrency: String): List<MarketCoin> {
        val offset = (page - 1) * perPage
        val cached = dao.getPaged(perPage, offset)
        val now = System.currentTimeMillis()
        val ttl = 60000L // 1 min

        return if (cached.isNotEmpty() && cached.all { now - it.updatedAt < ttl }) {
            cached.map { it.toDomain() }
        } else {
            val fresh = api.getMarkets(vsCurrency = vsCurrency, perPage = perPage, page = page)
                .map { it.toDomain().toEntity(now) }
            dao.upsertAll(fresh)
            fresh.map { it.toDomain() }
        }
    }

    override suspend fun refreshMarkets(page: Int, perPage: Int, vsCurrency: String): List<MarketCoin> {
        val now = System.currentTimeMillis()
        val fresh = api.getMarkets(vsCurrency = vsCurrency, perPage = perPage, page = page)
            .map { it.toDomain().toEntity(now) }
        dao.upsertAll(fresh)
        return fresh.map { it.toDomain() }
    }
}