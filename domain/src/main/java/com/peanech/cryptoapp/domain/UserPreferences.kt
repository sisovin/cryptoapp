package com.peanech.cryptoapp.domain

data class UserPreferences(
    val currency: String = "usd",
    val theme: String = "system"
)