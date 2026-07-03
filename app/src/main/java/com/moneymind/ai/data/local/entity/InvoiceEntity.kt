package com.moneymind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.moneymind.ai.domain.model.InvoiceStatus

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clientId")]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long,
    val invoiceNumber: String,
    val projectName: String? = null,
    val amount: Double,
    val status: InvoiceStatus = InvoiceStatus.SENT,
    val issueDateMillis: Long = System.currentTimeMillis(),
    val dueDateMillis: Long? = null,
    val notes: String? = null
)
