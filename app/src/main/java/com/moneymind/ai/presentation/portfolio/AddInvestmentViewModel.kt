package com.moneymind.ai.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.InvestmentEntity
import com.moneymind.ai.domain.model.InvestmentType
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.usecase.AddInvestmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddInvestmentUiState(
    val name: String = "",
    val type: InvestmentType = InvestmentType.STOCKS,
    val ledger: Ledger = Ledger.PERSONAL,
    val investedAmountText: String = "",
    val currentValueText: String = "",
    val notes: String = "",
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddInvestmentViewModel @Inject constructor(
    private val addInvestmentUseCase: AddInvestmentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddInvestmentUiState())
    val uiState: StateFlow<AddInvestmentUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, error = null)
    }

    fun onTypeChange(type: InvestmentType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun onLedgerChange(ledger: Ledger) {
        _uiState.value = _uiState.value.copy(ledger = ledger)
    }

    fun onInvestedAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(investedAmountText = value, error = null)
        }
    }

    fun onCurrentValueChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(currentValueText = value)
        }
    }

    fun onNotesChange(value: String) {
        _uiState.value = _uiState.value.copy(notes = value)
    }

    fun save() {
        val state = _uiState.value
        val invested = state.investedAmountText.toDoubleOrNull()

        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Enter an investment name")
            return
        }
        if (invested == null || invested <= 0.0) {
            _uiState.value = state.copy(error = "Enter a valid invested amount")
            return
        }

        // If current value isn't given yet, assume it equals what was invested.
        val currentValue = state.currentValueText.toDoubleOrNull() ?: invested

        viewModelScope.launch {
            addInvestmentUseCase(
                InvestmentEntity(
                    name = state.name.trim(),
                    type = state.type,
                    ledger = state.ledger,
                    investedAmount = invested,
                    currentValue = currentValue,
                    notes = state.notes.trim().ifBlank { null }
                )
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
