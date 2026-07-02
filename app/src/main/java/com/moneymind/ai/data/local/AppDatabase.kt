package com.moneymind.ai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.moneymind.ai.data.local.dao.InvestmentDao
import com.moneymind.ai.data.local.dao.LiabilityDao
import com.moneymind.ai.data.local.dao.LoanDao
import com.moneymind.ai.data.local.dao.SubscriptionDao
import com.moneymind.ai.data.local.dao.TransactionDao
import com.moneymind.ai.data.local.dao.UserPreferencesDao
import com.moneymind.ai.data.local.entity.InvestmentEntity
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
 * This round added [InvestmentEntity] (portfolio tracking) and
 * [LiabilityEntity] / [LiabilityPaymentEntity] (credit cards, loans, EMIs
 * owed). Together with [LoanEntity] (money owed *to* the user), these round
 * out the app's full net-worth picture.
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
        LiabilityPaymentEntity::class
    ],
    version = 4,
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

    companion object {
        const val DATABASE_NAME = "moneymind_encrypted.db"
    }
}
