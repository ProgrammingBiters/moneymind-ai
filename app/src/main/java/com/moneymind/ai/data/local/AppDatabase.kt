package com.moneymind.ai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moneymind.ai.data.local.dao.UserPreferencesDao
import com.moneymind.ai.data.local.entity.UserPreferencesEntity

/**
 * Root Room database. Opened through a SQLCipher [net.sqlcipher.database.SupportFactory]
 * (wired in [com.moneymind.ai.di.DatabaseModule]), so the .db file on disk is
 * fully encrypted at rest — the passphrase itself only ever lives inside the
 * Keystore-backed [com.moneymind.ai.core.security.SecureStore].
 *
 * Module 2 will add TransactionEntity, LedgerEntity, CategoryEntity, etc.
 * to this @Database annotation and bump the version with a Migration.
 */
@Database(
    entities = [UserPreferencesEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao

    companion object {
        const val DATABASE_NAME = "moneymind_encrypted.db"
    }
}
