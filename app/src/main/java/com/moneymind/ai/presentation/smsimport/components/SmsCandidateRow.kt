package com.moneymind.ai.presentation.smsimport.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moneymind.ai.core.util.CurrencyFormatter
import com.moneymind.ai.domain.model.ParsedSmsTransaction
import com.moneymind.ai.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SmsCandidateRow(candidate: ParsedSmsTransaction, onToggle: () -> Unit) {
    val isIncome = candidate.type == TransactionType.INCOME
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(checked = candidate.selected, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                Text(
                    text = candidate.merchantGuess ?: candidate.bankGuess ?: candidate.sender,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = SimpleDateFormat("d MMM, h:mm a", Locale.getDefault()).format(Date(candidate.dateMillis)) +
                        " · Confidence ${(candidate.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = (if (isIncome) "+" else "-") + CurrencyFormatter.format(candidate.amount),
                style = MaterialTheme.typography.titleMedium,
                color = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
