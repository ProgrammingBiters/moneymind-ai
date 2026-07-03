package com.moneymind.ai.domain.model

/** The two books the app keeps separately, per the dual-ledger design. */
enum class Ledger(val displayName: String) {
    PERSONAL("Personal"),
    BUSINESS("Business")
}
