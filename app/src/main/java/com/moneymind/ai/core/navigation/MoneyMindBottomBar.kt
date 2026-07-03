package com.moneymind.ai.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue

private data class BottomNavEntry(val screen: Screen, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomNavEntries = listOf(
    BottomNavEntry(Screen.Home, "Dashboard", Icons.Filled.Home),
    BottomNavEntry(Screen.Loans, "Loans", Icons.Filled.AccountBalanceWallet),
    BottomNavEntry(Screen.Subscriptions, "Subscriptions", Icons.Filled.Subscriptions),
    BottomNavEntry(Screen.Portfolio, "Portfolio", Icons.Filled.PieChart),
    BottomNavEntry(Screen.Business, "Business", Icons.Filled.Business)
)

/** Shared bottom nav shown on the three main tabs (Dashboard, Loans, Subscriptions). */
@Composable
fun MoneyMindBottomBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        bottomNavEntries.forEach { entry ->
            NavigationBarItem(
                selected = currentRoute == entry.screen.route,
                onClick = {
                    if (currentRoute != entry.screen.route) {
                        navController.navigate(entry.screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(entry.icon, contentDescription = entry.label) },
                label = { Text(entry.label) }
            )
        }
    }
}
