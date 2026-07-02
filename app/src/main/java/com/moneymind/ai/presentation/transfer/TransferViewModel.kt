package com.moneymind.ai.presentation.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.usecase.TransferBetweenLedgersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransferUiState(
    val amountText: String = "",
    val from: Ledger = Ledger.PERSONAL,
    val to: Ledger = Ledger.BUSINESS,
    val note: String = "",
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferBetweenLedgersUseCase: TransferBetweenLedgersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    fun onAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(amountText = value, error = null)
        }
    }

    fun onFromChange(ledger: Ledger) {
        val state = _uiState.value
        val newTo = if (ledger == state.to) oppositeOf(ledger) else state.to
        _uiState.value = state.copy(from = ledger, to = newTo)
    }

    fun onToChange(ledger: Ledger) {
        val state = _uiState.value
        val newFrom = if (ledger == state.from) oppositeOf(ledger) else state.from
        _uiState.value = state.copy(to = ledger, from = newFrom)
    }

    private fun oppositeOf(ledger: Ledger): Ledger =
        Ledger.entries.first { it != ledger }

    fun onNoteChange(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amountText.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.value = state.copy(error = "Enter a valid amount")
            return
        }
        if (state.from == state.to) {
            _uiState.value = state.copy(error = "Choose two different ledgers")
            return
        }

        viewModelScope.launch {
            transferBetweenLedgersUseCase(
                amount = amount,
                from = state.from,
                to = state.to,
                note = state.note.trim().ifBlank { null }
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
