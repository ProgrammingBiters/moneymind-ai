package com.moneymind.ai.presentation.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.SubscriptionEntity
import com.moneymind.ai.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubscriptionsUiState(
    val isLoading: Boolean = true,
    val subscriptions: List<SubscriptionEntity> = emptyList(),
    val estimatedMonthlyCost: Double = 0.0
)

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionsUiState())
    val uiState: StateFlow<SubscriptionsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val subscriptions = repository.getAllActive()
            val estimatedMonthlyCost = repository.getEstimatedMonthlyCost()
            _uiState.value = SubscriptionsUiState(
                isLoading = false,
                subscriptions = subscriptions,
                estimatedMonthlyCost = estimatedMonthlyCost
            )
        }
    }

    fun deleteSubscription(subscription: SubscriptionEntity) {
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
            refresh()
        }
    }
}
