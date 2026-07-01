package com.moneymind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.PaymentMode
import com.moneymind.ai.domain.model.TransactionCategory
import com.moneymind.ai.domain.model.TransactionType

/**
 * A single money movement: a manual entry, or one half of a ledger transfer.
 * Reused directly as the app's domain-level transaction model — there's no
 * separate DTO layer, since the shape genuinely doesn't differ between what
 * Room needs to persist and what the UI/use-cases need to work with.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val ledger: Ledger,
    val category: TransactionCategory,
    val paymentMode: PaymentMode,
    val note: String? = null,
    val merchant: String? = null,
    val dateMillis: Long,
    val createdAtMillis: Long = System.currentTimeMillis(),
    /** Non-null and shared between exactly two rows when this is one half of a transfer. */
    val transferGroupId: String? = null
)
