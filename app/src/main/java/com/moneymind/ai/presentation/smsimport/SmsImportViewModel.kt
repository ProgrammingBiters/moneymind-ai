package com.moneymind.ai.presentation.smsimport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.core.sms.SmsReader
import com.moneymind.ai.core.sms.SmsTransactionParser
import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.ParsedSmsTransaction
import com.moneymind.ai.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class SmsImportUiState(
    val isScanning: Boolean = false,
    val hasScanned: Boolean = false,
    val candidates: List<ParsedSmsTransaction> = emptyList(),
    val targetLedger: Ledger = Ledger.PERSONAL,
    val isImporting: Boolean = false,
    val importedCount: Int? = null
)

@HiltViewModel
class SmsImportViewModel @Inject constructor(
    private val smsReader: SmsReader,
    private val smsTransactionParser: SmsTransactionParser,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmsImportUiState())
    val uiState: StateFlow<SmsImportUiState> = _uiState.asStateFlow()

    /** Call once READ_SMS has been granted. Reading + regex parsing run off the main thread. */
    fun scan() {
        _uiState.value = _uiState.value.copy(isScanning = true)
        viewModelScope.launch {
            val candidates = withContext(Dispatchers.IO) {
                val raw = smsReader.readRecentInbox()
                smsTransactionParser.parseAll(raw)
            }
            _uiState.value = _uiState.value.copy(
                isScanning = false,
                hasScanned = true,
                candidates = candidates.sortedByDescending { it.dateMillis }
            )
        }
    }

    fun toggleSelected(id: String) {
        _uiState.value = _uiState.value.copy(
            candidates = _uiState.value.candidates.map {
                if (it.id == id) it.copy(selected = !it.selected) else it
            }
        )
    }

    fun selectAll(selected: Boolean) {
        _uiState.value = _uiState.value.copy(
            candidates = _uiState.value.candidates.map { it.copy(selected = selected) }
        )
    }

    fun setTargetLedger(ledger: Ledger) {
        _uiState.value = _uiState.value.copy(targetLedger = ledger)
    }

    fun importSelected() {
        val state = _uiState.value
        val toImport = state.candidates.filter { it.selected }
        if (toImport.isEmpty()) return

        _uiState.value = state.copy(isImporting = true)
        viewModelScope.launch {
            toImport.forEach { candidate ->
                addTransactionUseCase(
                    TransactionEntity(
                        amount = candidate.amount,
                        type = candidate.type,
                        ledger = state.targetLedger,
                        category = candidate.category,
                        paymentMode = candidate.paymentMode,
                        merchant = candidate.merchantGuess,
                        note = listOfNotNull(
                            "Imported from SMS",
                            candidate.bankGuess,
                            candidate.referenceGuess?.let { "Ref: $it" }
                        ).joinToString(" · "),
                        dateMillis = candidate.dateMillis
                    )
                )
            }
            _uiState.value = _uiState.value.copy(
                isImporting = false,
                importedCount = toImport.size,
                candidates = _uiState.value.candidates.filterNot { it.selected }
            )
        }
    }

    fun dismissImportedBanner() {
        _uiState.value = _uiState.value.copy(importedCount = null)
    }
}
