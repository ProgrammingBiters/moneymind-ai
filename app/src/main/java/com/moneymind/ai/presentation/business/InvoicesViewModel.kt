package com.moneymind.ai.presentation.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.ClientEntity
import com.moneymind.ai.data.local.entity.InvoiceEntity
import com.moneymind.ai.domain.repository.ClientRepository
import com.moneymind.ai.domain.repository.InvoiceRepository
import com.moneymind.ai.domain.usecase.MarkInvoicePaidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InvoiceListItem(
    val invoice: InvoiceEntity,
    val clientName: String
)

data class InvoicesUiState(
    val isLoading: Boolean = true,
    val items: List<InvoiceListItem> = emptyList(),
    val totalPending: Double = 0.0
)

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val clientRepository: ClientRepository,
    private val markInvoicePaidUseCase: MarkInvoicePaidUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoicesUiState())
    val uiState: StateFlow<InvoicesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val invoices = invoiceRepository.getAllInvoices()
            val clientCache = mutableMapOf<Long, ClientEntity?>()
            val items = invoices.map { invoice ->
                val client = clientCache.getOrPut(invoice.clientId) {
                    clientRepository.getClientById(invoice.clientId)
                }
                InvoiceListItem(invoice = invoice, clientName = client?.name ?: "Unknown Client")
            }
            _uiState.value = InvoicesUiState(
                isLoading = false,
                items = items,
                totalPending = invoiceRepository.getTotalPending()
            )
        }
    }

    fun markPaid(invoice: InvoiceEntity) {
        viewModelScope.launch {
            markInvoicePaidUseCase(invoice)
            refresh()
        }
    }
}
