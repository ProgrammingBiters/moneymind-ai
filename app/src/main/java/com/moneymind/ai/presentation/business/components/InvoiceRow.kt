package com.moneymind.ai.presentation.business.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moneymind.ai.core.util.CurrencyFormatter
import com.moneymind.ai.domain.model.InvoiceStatus
import com.moneymind.ai.presentation.business.InvoiceListItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceRow(item: InvoiceListItem, onMarkPaid: () -> Unit) {
    val isOverdue = item.invoice.status != InvoiceStatus.PAID &&
        item.invoice.dueDateMillis != null && item.invoice.dueDateMillis < System.currentTimeMillis()

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "${item.clientName} · #${item.invoice.invoiceNumber}", style = MaterialTheme.typography.titleMedium)
                item.invoice.projectName?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    text = when {
                        item.invoice.status == InvoiceStatus.PAID -> "Paid"
                        isOverdue -> "Overdue"
                        else -> item.invoice.status.displayName
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        item.invoice.status == InvoiceStatus.PAID -> MaterialTheme.colorScheme.primary
                        isOverdue -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = CurrencyFormatter.format(item.invoice.amount), style = MaterialTheme.typography.titleMedium)
                if (item.invoice.status != InvoiceStatus.PAID) {
                    IconButton(onClick = onMarkPaid) {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Mark as paid")
                    }
                }
            }
        }
    }
}
