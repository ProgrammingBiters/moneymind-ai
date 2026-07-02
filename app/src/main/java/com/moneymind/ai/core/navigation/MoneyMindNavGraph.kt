package com.moneymind.ai.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moneymind.ai.core.security.BiometricAuthManager
import com.moneymind.ai.presentation.auth.AuthViewModel
import com.moneymind.ai.presentation.auth.PinEntryScreen
import com.moneymind.ai.presentation.auth.PinSetupScreen
import com.moneymind.ai.presentation.dashboard.DashboardScreen
import com.moneymind.ai.presentation.loans.AddLoanScreen
import com.moneymind.ai.presentation.loans.LoanDetailScreen
import com.moneymind.ai.presentation.loans.LoansListScreen
import com.moneymind.ai.presentation.subscriptions.AddSubscriptionScreen
import com.moneymind.ai.presentation.subscriptions.SubscriptionsListScreen
import com.moneymind.ai.presentation.transaction.AddTransactionScreen
import com.moneymind.ai.presentation.transfer.TransferScreen

/**
 * Single top-level nav graph. Splash decides, based on [AuthViewModel],
 * whether to route into PIN setup (first run), PIN entry (returning +
 * locked), or straight to the Dashboard (already unlocked this session).
 *
 * Dashboard, Loans, and Subscriptions are the three main tabs, switched via
 * [MoneyMindBottomBar]. Add/Transfer/Detail screens are full-screen routes
 * pushed on top, without the bottom bar.
 */
@Composable
fun MoneyMindNavGraph(navController: NavHostController = rememberNavController()) {
    val biometricAuthManager = BiometricAuthManager()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val state = authViewModel.uiState.value

            val destination = when {
                !state.isPinSet -> Screen.PinSetup.route
                authViewModel.shouldRequireUnlock() -> Screen.PinEntry.route
                else -> Screen.Home.route
            }

            androidx.compose.runtime.LaunchedEffect(Unit) {
                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }

        composable(Screen.PinSetup.route) {
            PinSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PinSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PinEntry.route) {
            PinEntryScreen(
                biometricAuthManager = biometricAuthManager,
                onUnlocked = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PinEntry.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            DashboardScreen(navController = navController)
        }

        composable(Screen.Loans.route) {
            LoansListScreen(navController = navController)
        }

        composable(Screen.Subscriptions.route) {
            SubscriptionsListScreen(navController = navController)
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Transfer.route) {
            TransferScreen(
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddLoan.route) {
            AddLoanScreen(
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LoanDetail.route,
            arguments = listOf(navArgument("loanId") { type = NavType.LongType })
        ) {
            LoanDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.AddSubscription.route) {
            AddSubscriptionScreen(
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
