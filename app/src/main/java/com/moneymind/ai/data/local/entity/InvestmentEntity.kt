package com.moneymind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moneymind.ai.domain.model.InvestmentType
import com.moneymind.ai.domain.model.Ledger

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: InvestmentType,
    val ledger: Ledger,
    val investedAmount: Double,
    val currentValue: Double,
    val purchaseDateMillis: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val isActive: Boolean = true
)
