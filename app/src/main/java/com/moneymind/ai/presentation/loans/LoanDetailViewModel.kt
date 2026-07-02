package com.moneymind.ai.presentation.loans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.LoanEntity
import com.moneymind.ai.data.local.entity.LoanPaymentEntity
import com.moneymind.ai.domain.repository.LoanRepository
import com.moneymind.ai.domain.usecase.RecordLoanPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoanDetailUiState(
    val isLoading: Boolean = true,
    val loan: LoanEntity? = null,
    val payments: List<LoanPaymentEntity> = emptyList(),
    val totalRepaid: Double = 0.0,
    val outstanding: Double = 0.0,
    val paymentAmountText: String = "",
    val paymentNote: String = "",
    val paymentError: String? = null,
    val isDeleted: Boolean = false
)

@HiltViewModel
class LoanDetailViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val recordLoanPaymentUseCase: RecordLoanPaymentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val loanId: Long = savedStateHandle.get<Long>("loanId") ?: -1L

    private val _uiState = MutableStateFlow(LoanDetailUiState())
    val uiState: StateFlow<LoanDetailUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val loan = loanRepository.getLoanById(loanId)
            val payments = loanRepository.getPaymentsForLoan(loanId)
            val totalRepaid = loanRepository.getTotalRepaid(loanId)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                loan = loan,
                payments = payments,
                totalRepaid = totalRepaid,
                outstanding = (loan?.principalAmount ?: 0.0) - totalRepaid
            )
        }
    }

    fun onPaymentAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(paymentAmountText = value, paymentError = null)
        }
    }

    fun onPaymentNoteChange(value: String) {
        _uiState.value = _uiState.value.copy(paymentNote = value)
    }

    fun recordPayment() {
        val state = _uiState.value
        val amount = state.paymentAmountText.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.value = state.copy(paymentError = "Enter a valid amount")
            return
        }
        viewModelScope.launch {
            recordLoanPaymentUseCase(loanId, amount, state.paymentNote.trim().ifBlank { null })
            _uiState.value = _uiState.value.copy(paymentAmountText = "", paymentNote = "")
            refresh()
        }
    }

    fun deleteLoan() {
        viewModelScope.launch {
            _uiState.value.loan?.let { loanRepository.deleteLoan(it) }
            _uiState.value = _uiState.value.copy(isDeleted = true)
        }
    }
}
