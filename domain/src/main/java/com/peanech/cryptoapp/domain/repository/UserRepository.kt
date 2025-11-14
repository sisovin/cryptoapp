package com.peanech.cryptoapp.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.peanech.cryptoapp.domain.UserPreferences

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