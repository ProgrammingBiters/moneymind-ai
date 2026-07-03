package com.moneymind.ai.presentation.business

import androidx.lifecycle.SavedStateHandle
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

data class ClientDetailUiState(
    val isLoading: Boolean = true,
    val client: ClientEntity? = null,
    val invoices: List<InvoiceEntity> = emptyList(),
    val pendingTotal: Double = 0.0,
    val isDeleted: Boolean = false
)

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val invoiceRepository: InvoiceRepository,
    private val markInvoicePaidUseCase: MarkInvoicePaidUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val clientId: Long = savedStateHandle.get<Long>("clientId") ?: -1L

    private val _uiState = MutableStateFlow(ClientDetailUiState())
    val uiState: StateFlow<ClientDetailUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val client = clientRepository.getClientById(clientId)
            val invoices = invoiceRepository.getInvoicesForClient(clientId)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                client = client,
                invoices = invoices,
                pendingTotal = invoiceRepository.getPendingTotalForClient(clientId)
            )
        }
    }

    fun markInvoicePaid(invoice: InvoiceEntity) {
        viewModelScope.launch {
            markInvoicePaidUseCase(invoice)
            refresh()
        }
    }

    fun deleteClient() {
        viewModelScope.launch {
            _uiState.value.client?.let { clientRepository.deleteClient(it) }
            _uiState.value = _uiState.value.copy(isDeleted = true)
        }
    }
}
