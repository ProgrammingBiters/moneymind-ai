package com.moneymind.ai.domain.model

enum class BillingCycle(val displayName: String, val approxDays: Int) {
    WEEKLY("Weekly", 7),
    MONTHLY("Monthly", 30),
    YEARLY("Yearly", 365)
}
