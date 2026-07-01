package com.moneymind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row table (id is always 0) holding user-level app settings.
 * Kept in the encrypted database, not SharedPreferences, since fields like
 * currency/ledger defaults are meaningful financial data, not just UI state.
 */
@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: Int = 0,
    val currencyCode: String = "INR",
    val isDarkMode: Boolean = false,
    val autoLockTimeoutMillis: Long = 60_000L,
    val biometricEnabled: Boolean = false,
    val defaultLedger: String = "PERSONAL"
)
