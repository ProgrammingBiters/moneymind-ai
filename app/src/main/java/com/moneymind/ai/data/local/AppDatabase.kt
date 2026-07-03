package com.moneymind.ai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.moneymind.ai.data.local.dao.ClientDao
import com.moneymind.ai.data.local.dao.InvestmentDao
import com.moneymind.ai.data.local.dao.InvoiceDao
import com.moneymind.ai.data.local.dao.LiabilityDao
import com.moneymind.ai.data.local.dao.LoanDao
import com.moneymind.ai.data.local.dao.SubscriptionDao
import com.moneymind.ai.data.local.dao.TransactionDao
import com.moneymind.ai.data.local.dao.UserPreferencesDao
import com.moneymind.ai.data.local.entity.ClientEntity
import com.moneymind.ai.data.local.entity.InvestmentEntity
import com.moneymind.ai.data.local.entity.InvoiceEntity
import com.moneymind.ai.data.local.entity.LiabilityEntity
import com.moneymind.ai.data.local.entity.LiabilityPaymentEntity
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
 * This round added [ClientEntity] / [InvoiceEntity] — a lightweight Client
 * CRM (clients, invoices, pending-payment tracking). Full profit-per-client
 * P&L (linking business expenses to specific clients/projects) is a deeper
 * schema change deferred to a later round.
 */
@Database(
    entities = [
        UserPreferencesEntity::class,
        TransactionEntity::class,
        LoanEntity::class,
        LoanPaymentEntity::class,
        SubscriptionEntity::class,
        InvestmentEntity::class,
        LiabilityEntity::class,
        LiabilityPaymentEntity::class,
        ClientEntity::class,
        InvoiceEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun transactionDao(): TransactionDao
    abstract fun loanDao(): LoanDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun investmentDao(): InvestmentDao
    abstract fun liabilityDao(): LiabilityDao
    abstract fun clientDao(): ClientDao
    abstract fun invoiceDao(): InvoiceDao

    companion object {
        const val DATABASE_NAME = "moneymind_encrypted.db"
    }
}
