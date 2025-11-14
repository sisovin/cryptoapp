package com.peanech.cryptoapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.peanech.cryptoapp.ui.auth.AuthScreen
import com.peanech.cryptoapp.ui.dashboard.DashboardScreen
import com.peanech.cryptoapp.ui.detail.CoinDetailScreen
import com.peanech.cryptoapp.ui.settings.SettingsScreen

@Composable
fun CryptoApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(onAuthSuccess = { navController.navigate("dashboard") })
        }
        composable("dashboard") {
            DashboardScreen(
                onOpenDetail = { coinId -> navController.navigate("detail/$coinId") },
                onOpenSettings = { navController.navigate("settings") }
            )
        }
        composable("detail/{coinId}") { backStackEntry ->
            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
            CoinDetailScreen(coinId = coinId, onBack = { navController.popBackStack() })
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}