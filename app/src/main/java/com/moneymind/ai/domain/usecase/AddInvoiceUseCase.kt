package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.InvoiceEntity
import com.moneymind.ai.domain.repository.InvoiceRepository
import javax.inject.Inject

class AddInvoiceUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoice: InvoiceEntity): Long = repository.addInvoice(invoice)
}
