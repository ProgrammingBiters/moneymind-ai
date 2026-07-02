package com.moneymind.ai.core.security

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns the app's PIN lifecycle and inactivity-lock policy.
 *
 * PINs are never stored in plaintext or reversible form: each PIN is stretched
 * with PBKDF2WithHmacSHA256 (120,000 iterations, 256-bit output) against a
 * per-install random salt, and only the resulting hash + salt are persisted,
 * inside [SecureStore] (which itself is Keystore-encrypted).
 */
@Singleton
class AppLockManager @Inject constructor(
    private val secureStore: SecureStore
) {

    fun isPinSet(): Boolean = secureStore.contains(SecureStore.KEY_PIN_HASH)

    fun setPin(pin: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = hashPin(pin, salt)
        secureStore.putString(SecureStore.KEY_PIN_SALT, Base64.getEncoder().encodeToString(salt))
        secureStore.putString(SecureStore.KEY_PIN_HASH, Base64.getEncoder().encodeToString(hash))
        recordUnlock()
    }

    fun verifyPin(pin: String): Boolean {
        val storedSaltB64 = secureStore.getString(SecureStore.KEY_PIN_SALT) ?: return false
        val storedHashB64 = secureStore.getString(SecureStore.KEY_PIN_HASH) ?: return false
        val salt = Base64.getDecoder().decode(storedSaltB64)
        val expected = Base64.getDecoder().decode(storedHashB64)
        val actual = hashPin(pin, salt)
        val isValid = constantTimeEquals(expected, actual)
        if (isValid) recordUnlock()
        return isValid
    }

    fun recordUnlock() {
        secureStore.putLong(SecureStore.KEY_LAST_UNLOCK_AT, System.currentTimeMillis())
    }

    /**
     * True if the app should demand PIN/biometric re-entry: either it has never
     * been unlocked this session, or more than [timeoutMillis] has elapsed
     * since the last successful unlock (i.e. the "auto lock after inactivity"
     * requirement).
     */
    fun shouldRequireUnlock(timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS): Boolean {
        val lastUnlock = secureStore.getLong(SecureStore.KEY_LAST_UNLOCK_AT, -1L)
        if (lastUnlock < 0) return true
        return System.currentTimeMillis() - lastUnlock > timeoutMillis
    }

    fun resetPin() {
        secureStore.remove(SecureStore.KEY_PIN_HASH)
        secureStore.remove(SecureStore.KEY_PIN_SALT)
        secureStore.remove(SecureStore.KEY_LAST_UNLOCK_AT)
    }

    private fun hashPin(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var result = 0
        for (i in a.indices) result = result or (a[i].toInt() xor b[i].toInt())
        return result == 0
    }

    companion object {
        private const val PBKDF2_ITERATIONS = 120_000
        private const val KEY_LENGTH_BITS = 256
        const val DEFAULT_TIMEOUT_MILLIS = 60_000L // 1 minute of inactivity
    }
}
