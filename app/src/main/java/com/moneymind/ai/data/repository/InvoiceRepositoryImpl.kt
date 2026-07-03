package com.moneymind.ai.data.repository

import com.moneymind.ai.data.local.dao.InvoiceDao
import com.moneymind.ai.data.local.entity.InvoiceEntity
import com.moneymind.ai.domain.repository.InvoiceRepository
import javax.inject.Inject

class InvoiceRepositoryImpl @Inject constructor(
    private val dao: InvoiceDao
) : InvoiceRepository {
    override suspend fun addInvoice(invoice: InvoiceEntity): Long = dao.insert(invoice)
    override suspend fun updateInvoice(invoice: InvoiceEntity) = dao.update(invoice)
    override suspend fun deleteInvoice(invoice: InvoiceEntity) = dao.delete(invoice)
    override suspend fun getAllInvoices(): List<InvoiceEntity> = dao.getAll()
    override suspend fun getInvoicesForClient(clientId: Long): List<InvoiceEntity> = dao.getByClient(clientId)
    override suspend fun getPendingTotalForClient(clientId: Long): Double = dao.getPendingTotalForClient(clientId)
    override suspend fun getTotalPending(): Double = dao.getTotalPending()
}
