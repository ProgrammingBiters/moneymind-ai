package com.moneymind.ai.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.LiabilityEntity
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.LiabilityType
import com.moneymind.ai.domain.usecase.AddLiabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddLiabilityUiState(
    val name: String = "",
    val type: LiabilityType = LiabilityType.CREDIT_CARD,
    val ledger: Ledger = Ledger.PERSONAL,
    val totalAmountText: String = "",
    val interestRateText: String = "",
    val dueInDaysText: String = "",
    val notes: String = "",
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddLiabilityViewModel @Inject constructor(
    private val addLiabilityUseCase: AddLiabilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLiabilityUiState())
    val uiState: StateFlow<AddLiabilityUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, error = null)
    }

    fun onTypeChange(type: LiabilityType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun onLedgerChange(ledger: Ledger) {
        _uiState.value = _uiState.value.copy(ledger = ledger)
    }

    fun onTotalAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(totalAmountText = value, error = null)
        }
    }

    fun onInterestRateChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(interestRateText = value)
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
        val total = state.totalAmountText.toDoubleOrNull()

        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Enter a name for this liability")
            return
        }
        if (total == null || total <= 0.0) {
            _uiState.value = state.copy(error = "Enter a valid amount")
            return
        }

        val dueDateMillis = state.dueInDaysText.toIntOrNull()?.let { days ->
            System.currentTimeMillis() + days.toLong() * 24 * 60 * 60 * 1000
        }

        viewModelScope.launch {
            addLiabilityUseCase(
                LiabilityEntity(
                    name = state.name.trim(),
                    type = state.type,
                    ledger = state.ledger,
                    totalAmount = total,
                    interestRatePercent = state.interestRateText.toDoubleOrNull(),
                    dueDateMillis = dueDateMillis,
                    notes = state.notes.trim().ifBlank { null }
                )
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
