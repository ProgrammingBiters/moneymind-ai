package com.moneymind.ai.presentation.dashboard

import com.moneymind.ai.domain.model.Ledger

enum class LedgerFilter(val label: String, val ledger: Ledger?) {
    ALL("All", null),
    PERSONAL("Personal", Ledger.PERSONAL),
    BUSINESS("Business", Ledger.BUSINESS)
}
