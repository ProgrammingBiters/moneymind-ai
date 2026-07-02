package com.moneymind.ai.presentation.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.PaymentMode
import com.moneymind.ai.domain.model.TransactionCategory
import com.moneymind.ai.domain.model.TransactionType
import com.moneymind.ai.presentation.transaction.components.EnumDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = state.type == TransactionType.EXPENSE,
                    onClick = { viewModel.onTypeChange(TransactionType.EXPENSE) },
                    shape = SegmentedButtonDefaults.itemShape(0, 2)
                ) { Text("Expense") }
                SegmentedButton(
                    selected = state.type == TransactionType.INCOME,
                    onClick = { viewModel.onTypeChange(TransactionType.INCOME) },
                    shape = SegmentedButtonDefaults.itemShape(1, 2)
                ) { Text("Income") }
            }

            OutlinedTextField(
                value = state.amountText,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.error != null,
                supportingText = { state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                Ledger.entries.forEachIndexed { index, ledger ->
                    SegmentedButton(
                        selected = state.ledger == ledger,
                        onClick = { viewModel.onLedgerChange(ledger) },
                        shape = SegmentedButtonDefaults.itemShape(index, Ledger.entries.size)
                    ) { Text(ledger.displayName) }
                }
            }

            EnumDropdown(
                label = "Category",
                options = TransactionCategory.entries,
                selected = state.category,
                optionLabel = { it.displayName },
                onSelected = viewModel::onCategoryChange,
                modifier = Modifier.fillMaxWidth()
            )

            EnumDropdown(
                label = "Payment Mode",
                options = PaymentMode.entries,
                selected = state.paymentMode,
                optionLabel = { it.displayName },
                onSelected = viewModel::onPaymentModeChange,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.merchant,
                onValueChange = viewModel::onMerchantChange,
                label = { Text("Merchant / Payee (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text("Save Transaction")
            }
        }
    }
}
