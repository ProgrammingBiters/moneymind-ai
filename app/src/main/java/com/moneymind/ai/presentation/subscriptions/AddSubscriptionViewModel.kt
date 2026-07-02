package com.moneymind.ai.presentation.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.SubscriptionEntity
import com.moneymind.ai.domain.model.BillingCycle
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.usecase.AddSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddSubscriptionUiState(
    val name: String = "",
    val amountText: String = "",
    val billingCycle: BillingCycle = BillingCycle.MONTHLY,
    val ledger: Ledger = Ledger.PERSONAL,
    val renewsInDaysText: String = "",
    val notes: String = "",
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddSubscriptionViewModel @Inject constructor(
    private val addSubscriptionUseCase: AddSubscriptionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSubscriptionUiState())
    val uiState: StateFlow<AddSubscriptionUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, error = null)
    }

    fun onAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.value = _uiState.value.copy(amountText = value, error = null)
        }
    }

    fun onBillingCycleChange(cycle: BillingCycle) {
        _uiState.value = _uiState.value.copy(billingCycle = cycle)
    }

    fun onLedgerChange(ledger: Ledger) {
        _uiState.value = _uiState.value.copy(ledger = ledger)
    }

    fun onRenewsInDaysChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*$"))) {
            _uiState.value = _uiState.value.copy(renewsInDaysText = value)
        }
    }

    fun onNotesChange(value: String) {
        _uiState.value = _uiState.value.copy(notes = value)
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amountText.toDoubleOrNull()

        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Enter a subscription name")
            return
        }
        if (amount == null || amount <= 0.0) {
            _uiState.value = state.copy(error = "Enter a valid amount")
            return
        }

        val daysUntilRenewal = state.renewsInDaysText.toIntOrNull() ?: state.billingCycle.approxDays
        val nextRenewalMillis = System.currentTimeMillis() + daysUntilRenewal.toLong() * 24 * 60 * 60 * 1000

        viewModelScope.launch {
            addSubscriptionUseCase(
                SubscriptionEntity(
                    name = state.name.trim(),
                    amount = amount,
                    billingCycle = state.billingCycle,
                    ledger = state.ledger,
                    nextRenewalMillis = nextRenewalMillis,
                    notes = state.notes.trim().ifBlank { null }
                )
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
