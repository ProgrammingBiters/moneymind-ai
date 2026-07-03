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
    data object Portfolio : Screen("portfolio")
    data object AddInvestment : Screen("add_investment")
    data object AddLiability : Screen("add_liability")
    data object LiabilityDetail : Screen("liability_detail/{liabilityId}") {
        fun createRoute(liabilityId: Long) = "liability_detail/$liabilityId"
    }
    data object Reports : Screen("reports")
    data object SmsImport : Screen("sms_import")
    data object Business : Screen("business")
    data object AddClient : Screen("add_client")
    data object ClientDetail : Screen("client_detail/{clientId}") {
        fun createRoute(clientId: Long) = "client_detail/$clientId"
    }
    data object AddInvoice : Screen("add_invoice?clientId={clientId}") {
        fun createRoute(clientId: Long?) = "add_invoice?clientId=${clientId ?: -1L}"
    }
}

/** Routes that show the bottom navigation bar — the app's main tabs. */
val BOTTOM_NAV_ROUTES = setOf(
    Screen.Home.route,
    Screen.Loans.route,
    Screen.Subscriptions.route,
    Screen.Portfolio.route,
    Screen.Business.route
)
