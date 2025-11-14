package com.peanech.cryptoapp.data.repository

import com.peanech.cryptoapp.data.api.CoinGeckoService
import com.peanech.cryptoapp.data.database.CoinDetailDao
import com.peanech.cryptoapp.domain.CoinDetail
import com.peanech.cryptoapp.domain.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinGeckoService,
    private val dao: CoinDetailDao
) : CoinRepository {

    override suspend fun getCoinDetail(id: String, vsCurrency: String): CoinDetail {
        val cached = dao.get(id)
        val now = System.currentTimeMillis()
        val ttl = 300000L // 5 min

        return if (cached != null && now - cached.updatedAt < ttl) {
            cached.toDomain()
        } else {
            val fresh = api.getCoinDetail(id = id).toDomain(vsCurrency).toEntity(now)
            dao.upsert(fresh)
            fresh.toDomain()
        }
    }

    override suspend fun refreshCoinDetail(id: String, vsCurrency: String): CoinDetail {
        val now = System.currentTimeMillis()
        val fresh = api.getCoinDetail(id = id).toDomain(vsCurrency).toEntity(now)
        dao.upsert(fresh)
        return fresh.toDomain()
    }
}