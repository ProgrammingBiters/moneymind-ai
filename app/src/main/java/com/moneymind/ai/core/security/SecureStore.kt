package com.moneymind.ai.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps EncryptedSharedPreferences (AES-256-GCM, key held in the Android Keystore).
 * This is where secrets that must never touch plain disk live: the SQLCipher
 * database passphrase, the PIN hash + salt, and lock-state timestamps.
 *
 * Nothing here is ever synced off-device.
 */
@Singleton
class SecureStore @Inject constructor(
    @ApplicationContext context: Context
) {

    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "moneymind_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getString(key: String): String? = prefs.getString(key, null)

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getLong(key: String, default: Long = -1L): Long = prefs.getLong(key, default)

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun contains(key: String): Boolean = prefs.contains(key)

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    companion object {
        const val KEY_DB_PASSPHRASE = "db_passphrase"
        const val KEY_PIN_HASH = "pin_hash"
        const val KEY_PIN_SALT = "pin_salt"
        const val KEY_LAST_UNLOCK_AT = "last_unlock_at"
    }
}
