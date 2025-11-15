package com.peanech.cryptoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peanech.cryptoapp.domain.MarketCoin
import com.peanech.cryptoapp.network.ApiClient
import com.peanech.cryptoapp.network.toMarketCoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    private val _state = MutableStateFlow(DashboardState(isLoading = true))
    val state: StateFlow<DashboardState> = _state

    init {
        loadCoins()
    }

    private fun loadCoins() {
        viewModelScope.launch {
            try {
                val apiCoins = ApiClient.coinGeckoApi.getMarkets(
                    currency = _state.value.currency,
                    page = _state.value.page,
                    perPage = 50
                )
                val coins = apiCoins.map { it.toMarketCoin() }
                _state.value = _state.value.copy(
                    isLoading = false,
                    coins = coins,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load coins"
                )
            }
        }
    }

    fun onSearch(query: String) {
        _state.value = _state.value.copy(query = query)
    }

    fun onPaginate() {
        // TODO: Implement pagination
    }

    fun onRefresh() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        loadCoins()
    }

    fun onToggleWatch(coinId: String) {
        // TODO: Implement watchlist toggle
    }
}