package com.moneymind.ai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.moneymind.ai.data.local.dao.LoanDao
import com.moneymind.ai.data.local.dao.SubscriptionDao
import com.moneymind.ai.data.local.dao.TransactionDao
import com.moneymind.ai.data.local.dao.UserPreferencesDao
import com.moneymind.ai.data.local.entity.LoanEntity
import com.moneymind.ai.data.local.entity.LoanPaymentEntity
import com.moneymind.ai.data.local.entity.SubscriptionEntity
import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.data.local.entity.UserPreferencesEntity

/**
 * Root Room database. Opened through a SQLCipher [net.sqlcipher.database.SupportFactory]
 * (wired in [com.moneymind.ai.di.DatabaseModule]), so the .db file on disk is
 * fully encrypted at rest — the passphrase itself only ever lives inside the
 * Keystore-backed [com.moneymind.ai.core.security.SecureStore].
 *
 * Module 2 added [TransactionEntity]. This round added [LoanEntity] /
 * [LoanPaymentEntity] (Loan Tracker) and [SubscriptionEntity] (Subscription
 * Tracker). Future modules (investments, business/client CRM) will add their
 * own entities the same way.
 */
@Database(
    entities = [
        UserPreferencesEntity::class,
        TransactionEntity::class,
        LoanEntity::class,
        LoanPaymentEntity::class,
        SubscriptionEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun transactionDao(): TransactionDao
    abstract fun loanDao(): LoanDao
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        const val DATABASE_NAME = "moneymind_encrypted.db"
    }
}
