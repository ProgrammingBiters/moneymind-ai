package com.moneymind.ai.presentation.loans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.LoanEntity
import com.moneymind.ai.domain.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoanListItem(
    val loan: LoanEntity,
    val totalRepaid: Double,
    val outstanding: Double
)

data class LoansUiState(
    val isLoading: Boolean = true,
    val items: List<LoanListItem> = emptyList()
)

@HiltViewModel
class LoansViewModel @Inject constructor(
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoansUiState())
    val uiState: StateFlow<LoansUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val loans = loanRepository.getAllLoans()
            val items = loans.map { loan ->
                val repaid = loanRepository.getTotalRepaid(loan.id)
                LoanListItem(loan = loan, totalRepaid = repaid, outstanding = loan.principalAmount - repaid)
            }
            _uiState.value = LoansUiState(isLoading = false, items = items)
        }
    }
}
