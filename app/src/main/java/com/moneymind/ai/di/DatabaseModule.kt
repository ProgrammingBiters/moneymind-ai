package com.moneymind.ai.di

import android.content.Context
import androidx.room.Room
import com.moneymind.ai.core.security.SecureStore
import com.moneymind.ai.data.local.AppDatabase
import com.moneymind.ai.data.local.dao.UserPreferencesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Returns the SQLCipher passphrase for this install, generating and
     * persisting a new cryptographically random 256-bit passphrase on first
     * launch. The passphrase is stored only inside [SecureStore], i.e.
     * encrypted at rest by the Android Keystore — it never appears in
     * plaintext outside process memory.
     */
    private fun getOrCreatePassphrase(secureStore: SecureStore): ByteArray {
        val existing = secureStore.getString(SecureStore.KEY_DB_PASSPHRASE)
        if (existing != null) {
            return SQLiteDatabase.getBytes(existing.toCharArray())
        }
        val randomBytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val passphrase = randomBytes.joinToString("") { "%02x".format(it) }
        secureStore.putString(SecureStore.KEY_DB_PASSPHRASE, passphrase)
        return SQLiteDatabase.getBytes(passphrase.toCharArray())
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        secureStore: SecureStore
    ): AppDatabase {
        SQLiteDatabase.loadLibs(context)
        val passphrase = getOrCreatePassphrase(secureStore)
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .openHelperFactory(factory)
.fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesDao(database: AppDatabase): UserPreferencesDao =
        database.userPreferencesDao()
}
