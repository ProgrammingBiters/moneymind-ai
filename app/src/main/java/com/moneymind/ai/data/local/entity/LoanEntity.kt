package com.moneymind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moneymind.ai.domain.model.LoanDirection

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val direction: LoanDirection,
    val principalAmount: Double,
    val reason: String? = null,
    val interestRatePercent: Double? = null,
    val dueDateMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val isSettled: Boolean = false
)
