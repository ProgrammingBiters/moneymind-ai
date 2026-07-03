package com.moneymind.ai.presentation.loans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.LoanEntity
import com.moneymind.ai.domain.model.LoanDirection
import com.moneymind.ai.domain.usecase.AddLoanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddLoanUiState(
    val personName: String = "",
    val direction: LoanDirection = LoanDirection.GIVEN,
    val principalText: String = "",
    val interestRateText: String = "",
    val dueInDaysText: String = "",
    val reason: String = "",
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddLoanViewModel @Inject constructor(
    private val addLoanUseCase: AddLoanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLoanUiState())
    val uiState: StateFlow<AddLoanUiState> = _uiState.asStateFlow()

    fun onPersonNameChange(value: String) {
        _uiState.value = _uiState.value.copy(personName = value, error = null)
    }

    fun onDirectionChange(direction: LoanDirection) {
        _uiState.value = _uiState.value.copy(direction = direction)
    }

    fun onPrincipalChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(principalText = value, error = null)
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

    fun onReasonChange(value: String) {
        _uiState.value = _uiState.value.copy(reason = value)
    }

    fun save() {
        val state = _uiState.value
        val principal = state.principalText.toDoubleOrNull()

        if (state.personName.isBlank()) {
            _uiState.value = state.copy(error = "Enter who this loan is with")
            return
        }
        if (principal == null || principal <= 0.0) {
            _uiState.value = state.copy(error = "Enter a valid amount")
            return
        }

        val dueDateMillis = state.dueInDaysText.toIntOrNull()?.let { days ->
            System.currentTimeMillis() + days.toLong() * 24 * 60 * 60 * 1000
        }

        viewModelScope.launch {
            addLoanUseCase(
                LoanEntity(
                    personName = state.personName.trim(),
                    direction = state.direction,
                    principalAmount = principal,
                    reason = state.reason.trim().ifBlank { null },
                    interestRatePercent = state.interestRateText.toDoubleOrNull(),
                    dueDateMillis = dueDateMillis
                )
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
