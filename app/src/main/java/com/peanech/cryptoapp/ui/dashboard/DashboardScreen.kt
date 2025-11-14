package com.peanech.cryptoapp.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.peanech.cryptoapp.coreui.ErrorView
import com.peanech.cryptoapp.coreui.Loading
import com.peanech.cryptoapp.domain.MarketCoin
import com.peanech.cryptoapp.ui.viewmodel.DashboardState
import com.peanech.cryptoapp.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onOpenSettings: () -> Unit,
    onOpenDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crypto App") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = state.query,
                onValueChange = viewModel::onSearch,
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            when {
                state.isLoading && state.coins.isEmpty() -> Loading()
                state.error != null -> ErrorView(state.error!!) { viewModel.onRefresh() }
                else -> CoinList(
                    coins = state.coins.filter { it.name.contains(state.query, ignoreCase = true) },
                    watchlist = state.watchlist,
                    onCoinClick = onOpenDetail,
                    onWatchToggle = viewModel::onToggleWatch
                )
            }
        }
    }
}

@Composable
fun CoinList(
    coins: List<MarketCoin>,
    watchlist: Set<String>,
    onCoinClick: (String) -> Unit,
    onWatchToggle: (String) -> Unit
) {
    LazyColumn {
        items(coins) { coin ->
            CoinItem(coin, watchlist.contains(coin.id), onCoinClick, onWatchToggle)
        }
    }
}

@Composable
fun CoinItem(
    coin: MarketCoin,
    isWatched: Boolean,
    onClick: (String) -> Unit,
    onWatchToggle: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(model = coin.imageUrl, contentDescription = coin.name, modifier = Modifier.size(40.dp))
        Column {
            Text(coin.name)
            Text(coin.symbol)
        }
        Text("${coin.currentPrice}")
        Text("${coin.change24hPct ?: 0}%")
        IconButton(onClick = { onWatchToggle(coin.id) }) {
            Icon(if (isWatched) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null)
        }
    }
}