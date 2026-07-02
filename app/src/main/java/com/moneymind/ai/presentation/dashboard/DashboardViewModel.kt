package com.moneymind.ai.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.dao.CategoryTotal
import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.core.util.DateRanges
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.TransactionType
import com.moneymind.ai.domain.repository.LoanRepository
import com.moneymind.ai.domain.repository.SubscriptionRepository
import com.moneymind.ai.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val personalBalance: Double = 0.0,
    val businessBalance: Double = 0.0,
    val netWorth: Double = 0.0,
    val todayIncome: Double = 0.0,
    val todayExpense: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val topCategories: List<CategoryTotal> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val ledgerFilter: LedgerFilter = LedgerFilter.ALL,
    val pendingToReceive: Double = 0.0,
    val upcomingSubscriptionsCount: Int = 0,
    val estimatedMonthlySubscriptionCost: Double = 0.0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val loanRepository: LoanRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun setLedgerFilter(filter: LedgerFilter) {
        _uiState.value = _uiState.value.copy(ledgerFilter = filter)
        refresh()
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            refresh()
        }
    }

    /** Balance = income - expense + transfers in - transfers out, all-time, for one ledger. */
    private suspend fun ledgerBalance(ledger: Ledger): Double {
        val (start, end) = DateRanges.allTime()
        val income = repository.getSum(ledger, TransactionType.INCOME, start, end)
        val expense = repository.getSum(ledger, TransactionType.EXPENSE, start, end)
        val transferIn = repository.getSum(ledger, TransactionType.TRANSFER_IN, start, end)
        val transferOut = repository.getSum(ledger, TransactionType.TRANSFER_OUT, start, end)
        return income - expense + transferIn - transferOut
    }

    /** Combined sum for the currently selected ledger filter (both ledgers if ALL). */
    private suspend fun filteredSum(type: TransactionType, start: Long, end: Long): Double {
        val filterLedger = _uiState.value.ledgerFilter.ledger
        return if (filterLedger != null) {
            repository.getSum(filterLedger, type, start, end)
        } else {
            repository.getSum(Ledger.PERSONAL, type, start, end) +
                repository.getSum(Ledger.BUSINESS, type, start, end)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val (todayStart, todayEnd) = DateRanges.today()
            val (monthStart, monthEnd) = DateRanges.thisMonth()

            val personalBalance = ledgerBalance(Ledger.PERSONAL)
            val businessBalance = ledgerBalance(Ledger.BUSINESS)

            val todayIncome = filteredSum(TransactionType.INCOME, todayStart, todayEnd)
            val todayExpense = filteredSum(TransactionType.EXPENSE, todayStart, todayEnd)
            val monthIncome = filteredSum(TransactionType.INCOME, monthStart, monthEnd)
            val monthExpense = filteredSum(TransactionType.EXPENSE, monthStart, monthEnd)

            val filterLedger = _uiState.value.ledgerFilter.ledger
            val topCategories = repository.getTopExpenseCategories(filterLedger, monthStart, monthEnd, 5)
            val recent = repository.getRecent(filterLedger, 15)

            val pendingToReceive = loanRepository.getTotalOutstandingGiven()
            val upcomingWindow = DateRanges.today().let { (start, _) -> start to (start + 7L * 24 * 60 * 60 * 1000) }
            val upcomingSubscriptions = subscriptionRepository.getRenewingBetween(upcomingWindow.first, upcomingWindow.second)
            val estimatedMonthlySubscriptionCost = subscriptionRepository.getEstimatedMonthlyCost()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                personalBalance = personalBalance,
                businessBalance = businessBalance,
                netWorth = personalBalance + businessBalance,
                todayIncome = todayIncome,
                todayExpense = todayExpense,
                monthIncome = monthIncome,
                monthExpense = monthExpense,
                topCategories = topCategories,
                recentTransactions = recent,
                pendingToReceive = pendingToReceive,
                upcomingSubscriptionsCount = upcomingSubscriptions.size,
                estimatedMonthlySubscriptionCost = estimatedMonthlySubscriptionCost
            )
        }
    }
}
