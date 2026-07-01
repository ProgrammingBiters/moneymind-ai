package com.moneymind.ai.core.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object PinSetup : Screen("pin_setup")
    data object PinEntry : Screen("pin_entry")
    data object Home : Screen("home")
    data object AddTransaction : Screen("add_transaction")
    data object Transfer : Screen("transfer")
    data object Loans : Screen("loans")
    data object AddLoan : Screen("add_loan")
    data object LoanDetail : Screen("loan_detail/{loanId}") {
        fun createRoute(loanId: Long) = "loan_detail/$loanId"
    }
    data object Subscriptions : Screen("subscriptions")
    data object AddSubscription : Screen("add_subscription")
}

/** Routes that show the bottom navigation bar — the app's main tabs. */
val BOTTOM_NAV_ROUTES = setOf(Screen.Home.route, Screen.Loans.route, Screen.Subscriptions.route)
