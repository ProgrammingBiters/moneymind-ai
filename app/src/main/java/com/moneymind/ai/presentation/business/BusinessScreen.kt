package com.moneymind.ai.presentation.business

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.moneymind.ai.core.navigation.MoneyMindBottomBar
import com.moneymind.ai.core.navigation.Screen
import com.moneymind.ai.core.util.CurrencyFormatter
import com.moneymind.ai.presentation.business.components.ClientRow
import com.moneymind.ai.presentation.business.components.InvoiceRow

private enum class BusinessTab(val label: String) { CLIENTS("Clients"), INVOICES("Invoices") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    navController: NavHostController,
    clientsViewModel: ClientsViewModel = hiltViewModel(),
    invoicesViewModel: InvoicesViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(BusinessTab.CLIENTS) }
    val clientsState by clientsViewModel.uiState.collectAsState()
    val invoicesState by invoicesViewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                clientsViewModel.refresh()
                invoicesViewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Business") }) },
        bottomBar = { MoneyMindBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val route = if (selectedTab == BusinessTab.CLIENTS) Screen.AddClient.route else Screen.AddInvoice.createRoute(null)
                navController.navigate(route)
            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedTab == BusinessTab.INVOICES) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Total Pending",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormatter.format(invoicesState.totalPending),
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }

            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    BusinessTab.entries.forEachIndexed { index, tab ->
                        SegmentedButton(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            shape = SegmentedButtonDefaults.itemShape(index, BusinessTab.entries.size)
                        ) { Text(tab.label) }
                    }
                }
            }

            if (selectedTab == BusinessTab.CLIENTS) {
                if (clientsState.items.isEmpty() && !clientsState.isLoading) {
                    item {
                        Text(
                            text = "No clients yet. Tap + to add one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(clientsState.items, key = { it.client.id }) { item ->
                        ClientRow(
                            item = item,
                            onClick = { navController.navigate(Screen.ClientDetail.createRoute(item.client.id)) }
                        )
                    }
                }
            } else {
                if (invoicesState.items.isEmpty() && !invoicesState.isLoading) {
                    item {
                        Text(
                            text = "No invoices yet. Add a client first, then create an invoice for them.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(invoicesState.items, key = { it.invoice.id }) { item ->
                        InvoiceRow(item = item, onMarkPaid = { invoicesViewModel.markPaid(item.invoice) })
                    }
                }
            }
        }
    }
}
