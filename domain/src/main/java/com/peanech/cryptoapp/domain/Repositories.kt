package com.peanech.cryptoapp.domain

import com.google.firebase.auth.FirebaseUser

interface MarketRepository {
    suspend fun getMarkets(page: Int, perPage: Int, vsCurrency: String): List<MarketCoin>
    suspend fun refreshMarkets(page: Int, perPage: Int, vsCurrency: String): List<MarketCoin>
}

interface CoinRepository {
    suspend fun getCoinDetail(id: String, vsCurrency: String): CoinDetail
    suspend fun refreshCoinDetail(id: String, vsCurrency: String): CoinDetail
}

interface UserRepository {
    suspend fun currentUser(): FirebaseUser?
    suspend fun signInWithGoogle(idToken: String): FirebaseUser
    suspend fun signInWithEmail(email: String, password: String): FirebaseUser
    suspend fun signOut()
    suspend fun getWatchlist(uid: String): Set<String>
    suspend fun addToWatchlist(uid: String, coinId: String)
    suspend fun removeFromWatchlist(uid: String, coinId: String)
    suspend fun getPreferences(uid: String): UserPreferences
    suspend fun setPreferences(uid: String, prefs: UserPreferences)
}