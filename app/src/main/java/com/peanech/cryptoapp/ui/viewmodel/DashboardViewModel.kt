package com.peanech.cryptoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peanech.cryptoapp.domain.MarketCoin
import com.peanech.cryptoapp.domain.MarketRepository
import com.peanech.cryptoapp.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val isLoading: Boolean = false,
    val coins: List<MarketCoin> = emptyList(),
    val page: Int = 1,
    val query: String = "",
    val watchlist: Set<String> = emptySet(),
    val currency: String = "usd",
    val error: String? = null
)

class DashboardViewModel @Inject constructor(
    private val marketRepo: MarketRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState(isLoading = true))
    val state: StateFlow<DashboardState> = _state

    init { loadInitial() }

    fun onSearch(query: String) {
        _state.value = _state.value.copy(query = query)
    }

    fun onPaginate() {
        val nextPage = _state.value.page + 1
        viewModelScope.launch { fetchPage(nextPage, append = true) }
    }

    fun onRefresh() {
        viewModelScope.launch { fetchPage(1, append = false) }
    }

    fun onToggleWatch(coinId: String) = viewModelScope.launch {
        val uid = userRepo.currentUser()?.uid ?: return@launch
        val watchlist = userRepo.getWatchlist(uid)
        if (coinId in watchlist) userRepo.removeFromWatchlist(uid, coinId)
        else userRepo.addToWatchlist(uid, coinId)
        _state.value = _state.value.copy(watchlist = userRepo.getWatchlist(uid))
    }

    private fun loadInitial() = viewModelScope.launch {
        val uid = userRepo.currentUser()?.uid
        val watch = if (uid != null) userRepo.getWatchlist(uid) else emptySet()
        _state.value = _state.value.copy(watchlist = watch)
        fetchPage(1, append = false)
    }

    private suspend fun fetchPage(page: Int, append: Boolean) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        runCatching { marketRepo.getMarkets(page, 50, _state.value.currency) }
            .onSuccess { data ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    page = page,
                    coins = if (append) _state.value.coins + data else data
                )
            }
            .onFailure { e -> _state.value = _state.value.copy(isLoading = false, error = e.message) }
    }
}