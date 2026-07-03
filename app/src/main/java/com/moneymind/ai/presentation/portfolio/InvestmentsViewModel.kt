package com.moneymind.ai.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.InvestmentEntity
import com.moneymind.ai.domain.repository.InvestmentRepository
import com.moneymind.ai.domain.usecase.UpdateInvestmentValueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InvestmentsUiState(
    val isLoading: Boolean = true,
    val investments: List<InvestmentEntity> = emptyList(),
    val totalInvested: Double = 0.0,
    val totalCurrentValue: Double = 0.0
)

@HiltViewModel
class InvestmentsViewModel @Inject constructor(
    private val repository: InvestmentRepository,
    private val updateInvestmentValueUseCase: UpdateInvestmentValueUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvestmentsUiState())
    val uiState: StateFlow<InvestmentsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val investments = repository.getAllActive()
            _uiState.value = InvestmentsUiState(
                isLoading = false,
                investments = investments,
                totalInvested = repository.getTotalInvested(),
                totalCurrentValue = repository.getTotalCurrentValue()
            )
        }
    }

    fun updateValue(investment: InvestmentEntity, newValue: Double) {
        viewModelScope.launch {
            updateInvestmentValueUseCase(investment, newValue)
            refresh()
        }
    }

    fun deleteInvestment(investment: InvestmentEntity) {
        viewModelScope.launch {
            repository.deleteInvestment(investment)
            refresh()
        }
    }
}
