package com.moneymind.ai.presentation.portfolio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moneymind.ai.core.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiabilityDetailScreen(
    onBack: () -> Unit,
    viewModel: LiabilityDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onBack()
    }

    val liability = state.liability

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(liability?.name ?: "Liability") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::deleteLiability) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete liability")
                    }
                }
            )
        }
    ) { padding ->
        if (liability == null) {
            if (!state.isLoading) {
                Text(
                    text = "Liability not found.",
                    modifier = Modifier.padding(padding).padding(24.dp)
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.format(state.remaining),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "${CurrencyFormatter.format(state.totalPaid)} paid of ${CurrencyFormatter.format(liability.totalAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    liability.dueDateMillis?.let {
                        Text(
                            text = "Next payment due: ${SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(it))}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    liability.interestRatePercent?.let {
                        Text(
                            text = "Interest: $it% per annum",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    liability.notes?.let {
                        Text(
                            text = "Notes: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            if (liability.isActive) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Record a Payment", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = state.paymentAmountText,
                            onValueChange = viewModel::onPaymentAmountChange,
                            label = { Text("Amount (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = state.paymentError != null,
                            supportingText = {
                                state.paymentError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                        OutlinedTextField(
                            value = state.paymentNote,
                            onValueChange = viewModel::onPaymentNoteChange,
                            label = { Text("Note (optional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                        Button(
                            onClick = viewModel::recordPayment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text("Record Payment")
                        }
                    }
                }
            }

            Text(text = "Payment History", style = MaterialTheme.typography.titleMedium)

            if (state.payments.isEmpty()) {
                Text(
                    text = "No payments recorded yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                state.payments.forEach { payment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(payment.dateMillis)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(text = CurrencyFormatter.format(payment.amount), style = MaterialTheme.typography.bodyMedium)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
