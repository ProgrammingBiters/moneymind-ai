package com.moneymind.ai.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.moneymind.ai.core.navigation.MoneyMindBottomBar
import com.moneymind.ai.core.navigation.Screen
import com.moneymind.ai.presentation.dashboard.components.CategoryBreakdownList
import com.moneymind.ai.presentation.dashboard.components.LedgerBalanceRow
import com.moneymind.ai.presentation.dashboard.components.NetWorthCard
import com.moneymind.ai.presentation.dashboard.components.QuickStatsRow
import com.moneymind.ai.presentation.dashboard.components.SummaryStatRow
import com.moneymind.ai.presentation.dashboard.components.TransactionRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Reload whenever the screen comes back into view (e.g. returning from Add/Transfer).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MoneyMind AI") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Transfer.route) }) {
                        Icon(imageVector = Icons.Filled.SwapHoriz, contentDescription = "Transfer between ledgers")
                    }
                }
            )
        },
        bottomBar = { MoneyMindBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddTransaction.route) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add transaction")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Column(modifier = Modifier.padding(top = 12.dp)) { NetWorthCard(netWorth = state.netWorth) } }

            item { LedgerBalanceRow(personalBalance = state.personalBalance, businessBalance = state.businessBalance) }

            item {
                QuickStatsRow(
                    pendingToReceive = state.pendingToReceive,
                    upcomingSubscriptionsCount = state.upcomingSubscriptionsCount,
                    estimatedMonthlySubscriptionCost = state.estimatedMonthlySubscriptionCost
                )
            }

            item {
                SingleChoiceSegmentedButtonRow {
                    LedgerFilter.entries.forEachIndexed { index, filter ->
                        SegmentedButton(
                            selected = state.ledgerFilter == filter,
                            onClick = { viewModel.setLedgerFilter(filter) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = LedgerFilter.entries.size)
                        ) {
                            Text(filter.label)
                        }
                    }
                }
            }

            item { SummaryStatRow(label = "Today", incomeValue = state.todayIncome, expenseValue = state.todayExpense) }
            item { SummaryStatRow(label = "This Month", incomeValue = state.monthIncome, expenseValue = state.monthExpense) }

            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Top Spending Categories", style = MaterialTheme.typography.titleMedium)
                        CategoryBreakdownList(
                            categories = state.topCategories,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (state.recentTransactions.isEmpty()) {
                item {
                    Text(
                        text = "No transactions yet. Tap + to add your first one.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.recentTransactions, key = { it.id }) { transaction ->
                    Column {
                        TransactionRow(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) }
                        )
                        HorizontalDivider()
                    }
                }
            }

            item { androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 72.dp)) }
        }
    }
}
