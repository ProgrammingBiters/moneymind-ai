package com.moneymind.ai.presentation.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.ClientEntity
import com.moneymind.ai.data.local.entity.InvoiceEntity
import com.moneymind.ai.domain.model.InvoiceStatus
import com.moneymind.ai.domain.repository.ClientRepository
import com.moneymind.ai.domain.usecase.AddInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddInvoiceUiState(
    val clients: List<ClientEntity> = emptyList(),
    val selectedClient: ClientEntity? = null,
    val invoiceNumber: String = "",
    val projectName: String = "",
    val amountText: String = "",
    val dueInDaysText: String = "",
    val notes: String = "",
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddInvoiceViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val addInvoiceUseCase: AddInvoiceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val preselectedClientId: Long? = savedStateHandle.get<Long>("clientId")?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(AddInvoiceUiState())
    val uiState: StateFlow<AddInvoiceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val clients = clientRepository.getAllClients()
            val preselected = preselectedClientId?.let { id -> clients.firstOrNull { it.id == id } }
            _uiState.value = _uiState.value.copy(
                clients = clients,
                selectedClient = preselected ?: clients.firstOrNull()
            )
        }
    }

    fun onClientChange(client: ClientEntity) {
        _uiState.value = _uiState.value.copy(selectedClient = client)
    }

    fun onInvoiceNumberChange(value: String) {
        _uiState.value = _uiState.value.copy(invoiceNumber = value, error = null)
    }

    fun onProjectNameChange(value: String) {
        _uiState.value = _uiState.value.copy(projectName = value)
    }

    fun onAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(amountText = value, error = null)
        }
    }

    fun onDueInDaysChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*$"))) {
            _uiState.value = _uiState.value.copy(dueInDaysText = value)
        }
    }

    fun onNotesChange(value: String) {
        _uiState.value = _uiState.value.copy(notes = value)
    }

    fun save() {
        val state = _uiState.value
        val client = state.selectedClient
        val amount = state.amountText.toDoubleOrNull()

        if (client == null) {
            _uiState.value = state.copy(error = "Add a client first")
            return
        }
        if (state.invoiceNumber.isBlank()) {
            _uiState.value = state.copy(error = "Enter an invoice number")
            return
        }
        if (amount == null || amount <= 0.0) {
            _uiState.value = state.copy(error = "Enter a valid amount")
            return
        }

        val dueDateMillis = state.dueInDaysText.toIntOrNull()?.let { days ->
            System.currentTimeMillis() + days.toLong() * 24 * 60 * 60 * 1000
        }

        viewModelScope.launch {
            addInvoiceUseCase(
                InvoiceEntity(
                    clientId = client.id,
                    invoiceNumber = state.invoiceNumber.trim(),
                    projectName = state.projectName.trim().ifBlank { null },
                    amount = amount,
                    status = InvoiceStatus.SENT,
                    dueDateMillis = dueDateMillis,
                    notes = state.notes.trim().ifBlank { null }
                )
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
