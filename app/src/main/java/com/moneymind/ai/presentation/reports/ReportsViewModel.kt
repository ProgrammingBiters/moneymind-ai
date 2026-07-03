package com.moneymind.ai.presentation.reports

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.core.util.CsvExporter
import com.moneymind.ai.data.local.dao.CategoryTotal
import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.domain.model.TransactionType
import com.moneymind.ai.domain.repository.TransactionRepository
import com.moneymind.ai.presentation.dashboard.LedgerFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class ReportsUiState(
    val isLoading: Boolean = true,
    val monthLabel: String = "",
    val ledgerFilter: LedgerFilter = LedgerFilter.ALL,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val categoryBreakdown: List<CategoryTotal> = emptyList(),
    val transactionCount: Int = 0,
    val exportedUri: Uri? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    /** Months back from the current month; 0 = this month. */
    private var monthOffset = 0

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private var cachedTransactions: List<TransactionEntity> = emptyList()

    init {
        refresh()
    }

    private fun monthRange(): Triple<Long, Long, String> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, monthOffset)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        val label = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis

        return Triple(start, end, label)
    }

    fun previousMonth() {
        monthOffset -= 1
        refresh()
    }

    fun nextMonth() {
        if (monthOffset < 0) {
            monthOffset += 1
            refresh()
        }
    }

    fun setLedgerFilter(filter: LedgerFilter) {
        _uiState.value = _uiState.value.copy(ledgerFilter = filter)
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val (start, end, label) = monthRange()
            val filterLedger = _uiState.value.ledgerFilter.ledger

            val transactions = repository.getInRange(filterLedger, start, end)
            cachedTransactions = transactions

            val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val breakdown = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .map { (category, txs) -> CategoryTotal(category, txs.sumOf { it.amount }) }
                .sortedByDescending { it.total }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                monthLabel = label,
                totalIncome = income,
                totalExpense = expense,
                categoryBreakdown = breakdown,
                transactionCount = transactions.size
            )
        }
    }

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            val fileName = "MoneyMind_${_uiState.value.monthLabel.replace(" ", "_")}.csv"
            val uri = CsvExporter.export(context, cachedTransactions, fileName)
            _uiState.value = _uiState.value.copy(exportedUri = uri)
        }
    }

    fun onExportHandled() {
        _uiState.value = _uiState.value.copy(exportedUri = null)
    }
}
