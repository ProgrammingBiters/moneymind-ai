package com.moneymind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moneymind.ai.domain.model.BillingCycle
import com.moneymind.ai.domain.model.Ledger

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val billingCycle: BillingCycle,
    val ledger: Ledger,
    val nextRenewalMillis: Long,
    val isActive: Boolean = true,
    val notes: String? = null
)
