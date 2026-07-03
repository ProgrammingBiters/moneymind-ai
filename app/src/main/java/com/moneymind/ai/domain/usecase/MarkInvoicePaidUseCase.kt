package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.InvoiceEntity
import com.moneymind.ai.domain.model.InvoiceStatus
import com.moneymind.ai.domain.repository.InvoiceRepository
import javax.inject.Inject

class MarkInvoicePaidUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoice: InvoiceEntity) {
        repository.updateInvoice(invoice.copy(status = InvoiceStatus.PAID))
    }
}
