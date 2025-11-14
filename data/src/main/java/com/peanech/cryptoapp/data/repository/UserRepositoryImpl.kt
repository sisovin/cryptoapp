package com.peanech.cryptoapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.peanech.cryptoapp.domain.UserPreferences
import com.peanech.cryptoapp.domain.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) : UserRepository {

    override suspend fun currentUser(): FirebaseUser? = auth.currentUser

    override suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential).await().user!!
    }

    override suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        return auth.signInWithEmailAndPassword(email, password).await().user!!
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun getWatchlist(uid: String): Set<String> {
        val snapshot = db.getReference("users/$uid/watchlist").get().await()
        return snapshot.children.mapNotNull { it.key }.toSet()
    }

    override suspend fun addToWatchlist(uid: String, coinId: String) {
        db.getReference("users/$uid/watchlist/$coinId").setValue(true).await()
    }

    override suspend fun removeFromWatchlist(uid: String, coinId: String) {
        db.getReference("users/$uid/watchlist/$coinId").removeValue().await()
    }

    override suspend fun getPreferences(uid: String): UserPreferences {
        val snapshot = db.getReference("users/$uid/preferences").get().await()
        return snapshot.getValue(UserPreferences::class.java) ?: UserPreferences()
    }

    override suspend fun setPreferences(uid: String, prefs: UserPreferences) {
        db.getReference("users/$uid/preferences").setValue(prefs).await()
    }
}