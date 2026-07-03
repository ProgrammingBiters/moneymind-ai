package com.moneymind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** One payment made against a [LiabilityEntity] (e.g. an EMI or credit card payment). */
@Entity(
    tableName = "liability_payments",
    foreignKeys = [
        ForeignKey(
            entity = LiabilityEntity::class,
            parentColumns = ["id"],
            childColumns = ["liabilityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("liabilityId")]
)
data class LiabilityPaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val liabilityId: Long,
    val amount: Double,
    val dateMillis: Long = System.currentTimeMillis(),
    val note: String? = null
)
