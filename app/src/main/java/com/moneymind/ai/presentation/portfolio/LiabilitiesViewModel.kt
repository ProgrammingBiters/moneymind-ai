package com.moneymind.ai.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.LiabilityEntity
import com.moneymind.ai.domain.repository.LiabilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiabilityListItem(
    val liability: LiabilityEntity,
    val totalPaid: Double,
    val remaining: Double
)

data class LiabilitiesUiState(
    val isLoading: Boolean = true,
    val items: List<LiabilityListItem> = emptyList(),
    val totalRemaining: Double = 0.0
)

@HiltViewModel
class LiabilitiesViewModel @Inject constructor(
    private val repository: LiabilityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiabilitiesUiState())
    val uiState: StateFlow<LiabilitiesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val liabilities = repository.getAllActive()
            val items = liabilities.map { liability ->
                val paid = repository.getTotalPaid(liability.id)
                LiabilityListItem(liability = liability, totalPaid = paid, remaining = liability.totalAmount - paid)
            }
            _uiState.value = LiabilitiesUiState(
                isLoading = false,
                items = items,
                totalRemaining = repository.getTotalRemaining()
            )
        }
    }
}
