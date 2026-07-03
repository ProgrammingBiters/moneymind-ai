package com.moneymind.ai.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moneymind.ai.core.util.CurrencyFormatter
import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionRow(
    transaction: TransactionEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPositive = transaction.type == TransactionType.INCOME || transaction.type == TransactionType.TRANSFER_IN
    val dateText = formatDate(transaction.dateMillis)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.merchant?.takeIf { it.isNotBlank() } ?: transaction.category.displayName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${transaction.category.displayName} · ${transaction.ledger.displayName} · $dateText",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = (if (isPositive) "+" else "-") + CurrencyFormatter.format(transaction.amount),
            style = MaterialTheme.typography.titleMedium,
            color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete transaction")
        }
    }
}

private fun formatDate(dateMillis: Long): String =
    SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(dateMillis))
