package com.moneymind.ai.domain.repository

import com.moneymind.ai.data.local.entity.InvoiceEntity

interface InvoiceRepository {
    suspend fun addInvoice(invoice: InvoiceEntity): Long
    suspend fun updateInvoice(invoice: InvoiceEntity)
    suspend fun deleteInvoice(invoice: InvoiceEntity)
    suspend fun getAllInvoices(): List<InvoiceEntity>
    suspend fun getInvoicesForClient(clientId: Long): List<InvoiceEntity>
    suspend fun getPendingTotalForClient(clientId: Long): Double
    suspend fun getTotalPending(): Double
}
