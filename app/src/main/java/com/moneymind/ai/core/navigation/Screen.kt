package com.moneymind.ai.core.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object PinSetup : Screen("pin_setup")
    data object PinEntry : Screen("pin_entry")
    data object Home : Screen("home")
}
