package com.peanech.cryptoapp.data.repository

import com.peanech.cryptoapp.data.api.CoinDetailDto
import com.peanech.cryptoapp.data.api.MarketCoinDto
import com.peanech.cryptoapp.data.database.CoinDetailEntity
import com.peanech.cryptoapp.data.database.MarketCoinEntity
import com.peanech.cryptoapp.domain.CoinDetail
import com.peanech.cryptoapp.domain.MarketCoin

fun MarketCoinDto.toDomain(): MarketCoin = MarketCoin(
    id = id,
    symbol = symbol,
    name = name,
    imageUrl = image,
    currentPrice = currentPrice,
    marketCap = marketCap,
    change24hPct = priceChangePercentage24h,
    high24h = high24h,
    low24h = low24h
)

fun MarketCoinEntity.toDomain(): MarketCoin = MarketCoin(
    id = id,
    symbol = symbol,
    name = name,
    imageUrl = imageUrl,
    currentPrice = currentPrice,
    marketCap = marketCap,
    change24hPct = change24hPct,
    high24h = high24h,
    low24h = low24h
)

fun MarketCoin.toEntity(updatedAt: Long = System.currentTimeMillis()): MarketCoinEntity = MarketCoinEntity(
    id = id,
    symbol = symbol,
    name = name,
    imageUrl = imageUrl,
    currentPrice = currentPrice,
    marketCap = marketCap,
    change24hPct = change24hPct,
    high24h = high24h,
    low24h = low24h,
    updatedAt = updatedAt
)

fun CoinDetailDto.toDomain(vsCurrency: String): CoinDetail = CoinDetail(
    id = id,
    symbol = symbol,
    name = name,
    imageSmall = image["small"] ?: "",
    currentPrice = marketData.currentPrice[vsCurrency] ?: 0.0,
    change24hPct = marketData.priceChangePercentage24h,
    high24h = marketData.high24h?.get(vsCurrency),
    low24h = marketData.low24h?.get(vsCurrency),
    sparkline7d = marketData.sparkline7d?.price ?: emptyList()
)

fun CoinDetailEntity.toDomain(): CoinDetail = CoinDetail(
    id = id,
    symbol = symbol,
    name = name,
    imageSmall = imageSmall,
    currentPrice = currentPrice,
    change24hPct = change24hPct,
    high24h = high24h,
    low24h = low24h,
    sparkline7d = getSparklineList()
)

fun CoinDetail.toEntity(updatedAt: Long = System.currentTimeMillis()): CoinDetailEntity = CoinDetailEntity(
    id = id,
    symbol = symbol,
    name = name,
    imageSmall = imageSmall,
    currentPrice = currentPrice,
    change24hPct = change24hPct,
    high24h = high24h,
    low24h = low24h,
    sparkline7d = CoinDetailEntity.createSparklineJson(sparkline7d),
    updatedAt = updatedAt
)