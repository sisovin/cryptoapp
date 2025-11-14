package com.peanech.cryptoapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

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

@Entity(tableName = "coin_details")
data class CoinDetailEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val name: String,
    val imageSmall: String,
    val currentPrice: Double,
    val change24hPct: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val sparkline7d: String, // JSON string of doubles
    val updatedAt: Long
) {
    fun getSparklineList(): List<Double> {
        val moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, Double::class.java)
        val adapter: JsonAdapter<List<Double>> = moshi.adapter(listType)
        return adapter.fromJson(sparkline7d) ?: emptyList()
    }

    companion object {
        fun createSparklineJson(list: List<Double>): String {
            val moshi = Moshi.Builder().build()
            val listType = Types.newParameterizedType(List::class.java, Double::class.java)
            val adapter: JsonAdapter<List<Double>> = moshi.adapter(listType)
            return adapter.toJson(list)
        }
    }
}