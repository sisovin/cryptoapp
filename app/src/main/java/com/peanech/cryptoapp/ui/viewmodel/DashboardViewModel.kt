package com.peanech.cryptoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peanech.cryptoapp.domain.MarketCoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DashboardState(
    val isLoading: Boolean = false,
    val coins: List<MarketCoin> = emptyList(),
    val page: Int = 1,
    val query: String = "",
    val watchlist: Set<String> = emptySet(),
    val currency: String = "usd",
    val error: String? = null
)

class DashboardViewModel() : ViewModel() {

    private val _state = MutableStateFlow(DashboardState(isLoading = false, coins = listOf(
        MarketCoin(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
            currentPrice = 45000.0,
            marketCap = 850000000000.0,
            change24hPct = 2.27,
            high24h = 46000.0,
            low24h = 44000.0
        ),
        MarketCoin(
            id = "ethereum",
            symbol = "eth",
            name = "Ethereum",
            imageUrl = "https://assets.coingecko.com/coins/images/279/large/ethereum.png",
            currentPrice = 2500.0,
            marketCap = 300000000000.0,
            change24hPct = 2.04,
            high24h = 2600.0,
            low24h = 2400.0
        )
    )))
    val state: StateFlow<DashboardState> = _state

    init {
        // No-op for now
    }

    fun onSearch(query: String) {
        // TODO: Implement search
    }

    fun onPaginate() {
        // TODO: Implement pagination
    }

    fun onRefresh() {
        // TODO: Implement refresh
    }

    fun onToggleWatch(coinId: String) {
        // TODO: Implement watchlist toggle
    }
}