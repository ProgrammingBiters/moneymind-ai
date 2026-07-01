package com.moneymind.ai.domain.model

/** Per the Loan Tracker spec: money the user has lent out to others, tracked until repaid. */
enum class LoanDirection(val displayName: String) {
    GIVEN("Money I Gave"),
    TAKEN("Money I Borrowed")
}
