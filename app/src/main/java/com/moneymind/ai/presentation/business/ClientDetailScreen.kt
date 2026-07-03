package com.moneymind.ai.presentation.business

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moneymind.ai.core.util.CurrencyFormatter
import com.moneymind.ai.presentation.business.components.InvoiceRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    onBack: () -> Unit,
    onAddInvoice: (Long) -> Unit,
    viewModel: ClientDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) onBack()
    }

    val client = state.client

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(client?.name ?: "Client") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::deleteClient) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete client")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddInvoice(viewModel.clientId) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add invoice")
            }
        }
    ) { padding ->
        if (client == null) {
            if (!state.isLoading) {
                Text(text = "Client not found.", modifier = Modifier.padding(padding).padding(24.dp))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    client.company?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
                    client.email?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
                    client.phone?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
                    client.notes?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Text(
                        text = "Pending: ${CurrencyFormatter.format(state.pendingTotal)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Text(text = "Invoices", style = MaterialTheme.typography.titleMedium)

            if (state.invoices.isEmpty()) {
                Text(
                    text = "No invoices for this client yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                state.invoices.forEach { invoice ->
                    InvoiceRow(
                        item = InvoiceListItem(invoice = invoice, clientName = client.name),
                        onMarkPaid = { viewModel.markInvoicePaid(invoice) }
                    )
                }
            }
        }
    }
}
