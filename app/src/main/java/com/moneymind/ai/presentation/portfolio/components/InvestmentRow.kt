package com.moneymind.ai.presentation.portfolio.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moneymind.ai.core.util.CurrencyFormatter
import com.moneymind.ai.data.local.entity.InvestmentEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentRow(
    investment: InvestmentEntity,
    onValueUpdated: (Double) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val gain = investment.currentValue - investment.investedAmount
    val isGain = gain >= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = investment.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${investment.type.displayName} · ${investment.ledger.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = CurrencyFormatter.format(investment.currentValue), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = (if (isGain) "+" else "") + CurrencyFormatter.format(gain),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isGain) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDialog) {
        var valueText by remember { mutableStateOf(investment.currentValue.toString()) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Update Current Value") },
            text = {
                OutlinedTextField(
                    value = valueText,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) valueText = it },
                    label = { Text("Current Value (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    valueText.toDoubleOrNull()?.let { onValueUpdated(it) }
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
