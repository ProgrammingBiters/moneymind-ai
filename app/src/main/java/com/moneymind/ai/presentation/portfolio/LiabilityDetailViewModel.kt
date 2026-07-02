package com.moneymind.ai.presentation.portfolio

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.LiabilityEntity
import com.moneymind.ai.data.local.entity.LiabilityPaymentEntity
import com.moneymind.ai.domain.repository.LiabilityRepository
import com.moneymind.ai.domain.usecase.RecordLiabilityPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiabilityDetailUiState(
    val isLoading: Boolean = true,
    val liability: LiabilityEntity? = null,
    val payments: List<LiabilityPaymentEntity> = emptyList(),
    val totalPaid: Double = 0.0,
    val remaining: Double = 0.0,
    val paymentAmountText: String = "",
    val paymentNote: String = "",
    val paymentError: String? = null,
    val isDeleted: Boolean = false
)

@HiltViewModel
class LiabilityDetailViewModel @Inject constructor(
    private val liabilityRepository: LiabilityRepository,
    private val recordLiabilityPaymentUseCase: RecordLiabilityPaymentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val liabilityId: Long = savedStateHandle.get<Long>("liabilityId") ?: -1L

    private val _uiState = MutableStateFlow(LiabilityDetailUiState())
    val uiState: StateFlow<LiabilityDetailUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val liability = liabilityRepository.getLiabilityById(liabilityId)
            val payments = liabilityRepository.getPaymentsForLiability(liabilityId)
            val totalPaid = liabilityRepository.getTotalPaid(liabilityId)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                liability = liability,
                payments = payments,
                totalPaid = totalPaid,
                remaining = (liability?.totalAmount ?: 0.0) - totalPaid
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
            recordLiabilityPaymentUseCase(liabilityId, amount, state.paymentNote.trim().ifBlank { null })
            _uiState.value = _uiState.value.copy(paymentAmountText = "", paymentNote = "")
            refresh()
        }
    }

    fun deleteLiability() {
        viewModelScope.launch {
            _uiState.value.liability?.let { liabilityRepository.deleteLiability(it) }
            _uiState.value = _uiState.value.copy(isDeleted = true)
        }
    }
}
