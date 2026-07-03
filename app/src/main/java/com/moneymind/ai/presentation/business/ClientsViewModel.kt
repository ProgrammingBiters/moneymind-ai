package com.moneymind.ai.presentation.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.ClientEntity
import com.moneymind.ai.domain.repository.ClientRepository
import com.moneymind.ai.domain.repository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientListItem(
    val client: ClientEntity,
    val pendingTotal: Double
)

data class ClientsUiState(
    val isLoading: Boolean = true,
    val items: List<ClientListItem> = emptyList()
)

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val invoiceRepository: InvoiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientsUiState())
    val uiState: StateFlow<ClientsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val clients = clientRepository.getAllClients()
            val items = clients.map { client ->
                ClientListItem(client = client, pendingTotal = invoiceRepository.getPendingTotalForClient(client.id))
            }
            _uiState.value = ClientsUiState(isLoading = false, items = items)
        }
    }
}
