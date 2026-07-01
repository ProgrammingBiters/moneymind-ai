package com.moneymind.ai.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moneymind.ai.core.security.BiometricAuthManager
import com.moneymind.ai.presentation.auth.AuthViewModel
import com.moneymind.ai.presentation.auth.PinEntryScreen
import com.moneymind.ai.presentation.auth.PinSetupScreen
import com.moneymind.ai.presentation.home.HomeScreen
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Single top-level nav graph for the skeleton: Splash decides, based on
 * [AuthViewModel], whether to route into PIN setup (first run), PIN entry
 * (returning + locked), or straight to Home (already unlocked this session).
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
            HomeScreen()
        }
    }
}
