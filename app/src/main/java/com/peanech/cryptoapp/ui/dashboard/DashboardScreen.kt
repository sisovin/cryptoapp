package com.peanech.cryptoapp.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.peanech.cryptoapp.coreui.ErrorView
import com.peanech.cryptoapp.coreui.Loading
import com.peanech.cryptoapp.domain.MarketCoin
import com.peanech.cryptoapp.ui.viewmodel.DashboardState
import com.peanech.cryptoapp.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onOpenSettings: () -> Unit,
    onOpenDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CryptoTracker",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Portfolio Summary Card
            PortfolioSummaryCard()

            // Search Bar
            TextField(
                value = state.query,
                onValueChange = viewModel::onSearch,
                placeholder = { Text("Search cryptocurrencies...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Content
            when {
                state.isLoading && state.coins.isEmpty() -> Loading()
                state.error != null -> ErrorView(
                    message = state.error!!,
                    onRetry = { viewModel.onRefresh() }
                )
                else -> CoinList(
                    coins = state.coins.filter {
                        it.name.contains(state.query, ignoreCase = true) ||
                        it.symbol.contains(state.query, ignoreCase = true)
                    },
                    watchlist = state.watchlist,
                    onCoinClick = onOpenDetail,
                    onWatchToggle = viewModel::onToggleWatch
                )
            }
        }
    }
}

@Composable
fun PortfolioSummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E3A8A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Portfolio Value",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$124,567.89",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "↗",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF10B981)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "+$2,345.67 (1.92%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF10B981)
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        )
    ) {
        items(coins) { coin ->
            CoinItemCard(
                coin = coin,
                isWatched = watchlist.contains(coin.id),
                onClick = onCoinClick,
                onWatchToggle = onWatchToggle
            )
        }
    }
}

@Composable
fun CoinItemCard(
    coin: MarketCoin,
    isWatched: Boolean,
    onClick: (String) -> Unit,
    onWatchToggle: (String) -> Unit
) {
    val priceChange = coin.change24hPct ?: 0.0
    val isPositive = priceChange >= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(coin.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Coin Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Coin Image
                AsyncImage(
                    model = coin.imageUrl,
                    contentDescription = coin.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Coin Details
                Column {
                    Text(
                        text = coin.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = coin.symbol.uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Price and Change
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$${String.format("%.2f", coin.currentPrice)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isPositive) "↗" else "↘",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format("%.2f", priceChange)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }

            // Watchlist Button
            IconButton(
                onClick = { onWatchToggle(coin.id) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    if (isWatched) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isWatched) "Remove from watchlist" else "Add to watchlist",
                    tint = if (isWatched) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}