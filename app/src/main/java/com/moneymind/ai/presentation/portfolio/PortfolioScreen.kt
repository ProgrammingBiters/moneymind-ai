package com.moneymind.ai.presentation.portfolio

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
import com.moneymind.ai.presentation.portfolio.components.InvestmentRow
import com.moneymind.ai.presentation.portfolio.components.LiabilityRow

private enum class PortfolioTab(val label: String) { INVESTMENTS("Investments"), LIABILITIES("Liabilities") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    navController: NavHostController,
    investmentsViewModel: InvestmentsViewModel = hiltViewModel(),
    liabilitiesViewModel: LiabilitiesViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(PortfolioTab.INVESTMENTS) }
    val investmentsState by investmentsViewModel.uiState.collectAsState()
    val liabilitiesState by liabilitiesViewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                investmentsViewModel.refresh()
                liabilitiesViewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Portfolio") }) },
        bottomBar = { MoneyMindBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val route = if (selectedTab == PortfolioTab.INVESTMENTS) Screen.AddInvestment.route else Screen.AddLiability.route
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
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (selectedTab == PortfolioTab.INVESTMENTS) "Total Portfolio Value" else "Total Owed",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyFormatter.format(
                                if (selectedTab == PortfolioTab.INVESTMENTS) investmentsState.totalCurrentValue
                                else liabilitiesState.totalRemaining
                            ),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        if (selectedTab == PortfolioTab.INVESTMENTS) {
                            val gain = investmentsState.totalCurrentValue - investmentsState.totalInvested
                            Text(
                                text = (if (gain >= 0) "+" else "") + CurrencyFormatter.format(gain) + " overall",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (gain >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    PortfolioTab.entries.forEachIndexed { index, tab ->
                        SegmentedButton(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            shape = SegmentedButtonDefaults.itemShape(index, PortfolioTab.entries.size)
                        ) { Text(tab.label) }
                    }
                }
            }

            if (selectedTab == PortfolioTab.INVESTMENTS) {
                if (investmentsState.investments.isEmpty() && !investmentsState.isLoading) {
                    item {
                        Text(
                            text = "No investments tracked yet. Tap + to add stocks, gold, FDs, property, and more.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(investmentsState.investments, key = { it.id }) { investment ->
                        InvestmentRow(
                            investment = investment,
                            onValueUpdated = { newValue -> investmentsViewModel.updateValue(investment, newValue) }
                        )
                    }
                }
            } else {
                if (liabilitiesState.items.isEmpty() && !liabilitiesState.isLoading) {
                    item {
                        Text(
                            text = "No liabilities tracked yet. Tap + to add credit cards, loans, or EMIs.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(liabilitiesState.items, key = { it.liability.id }) { item ->
                        LiabilityRow(
                            item = item,
                            onClick = { navController.navigate(Screen.LiabilityDetail.createRoute(item.liability.id)) }
                        )
                    }
                }
            }
        }
    }
}
