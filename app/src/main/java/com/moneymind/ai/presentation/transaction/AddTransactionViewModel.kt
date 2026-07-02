package com.moneymind.ai.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.PaymentMode
import com.moneymind.ai.domain.model.TransactionCategory
import com.moneymind.ai.domain.model.TransactionType
import com.moneymind.ai.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTransactionUiState(
    val amountText: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val ledger: Ledger = Ledger.PERSONAL,
    val category: TransactionCategory = TransactionCategory.OTHERS,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val merchant: String = "",
    val note: String = "",
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun onAmountChange(value: String) {
        // Only allow digits and a single decimal point, matching how money is actually typed.
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(amountText = value, error = null)
        }
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun onLedgerChange(ledger: Ledger) {
        _uiState.value = _uiState.value.copy(ledger = ledger)
    }

    fun onCategoryChange(category: TransactionCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun onPaymentModeChange(paymentMode: PaymentMode) {
        _uiState.value = _uiState.value.copy(paymentMode = paymentMode)
    }

    fun onMerchantChange(value: String) {
        _uiState.value = _uiState.value.copy(merchant = value)
    }

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

        viewModelScope.launch {
            addTransactionUseCase(
                TransactionEntity(
                    amount = amount,
                    type = state.type,
                    ledger = state.ledger,
                    category = state.category,
                    paymentMode = state.paymentMode,
                    merchant = state.merchant.trim().ifBlank { null },
                    note = state.note.trim().ifBlank { null },
                    dateMillis = System.currentTimeMillis()
                )
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
