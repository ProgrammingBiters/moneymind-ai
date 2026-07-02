package com.moneymind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.LiabilityType

@Entity(tableName = "liabilities")
data class LiabilityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: LiabilityType,
    val ledger: Ledger,
    val totalAmount: Double,
    val interestRatePercent: Double? = null,
    val dueDateMillis: Long? = null,
    val notes: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
