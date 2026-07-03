package com.moneymind.ai.core.util

import java.util.Locale

/**
 * Formats amounts as ₹ with grouping. Currency code will become configurable
 * via UserPreferencesEntity once the settings screen (later module) writes to it;
 * for now the app defaults to INR per the product spec.
 */
object CurrencyFormatter {
    fun format(amount: Double): String {
        val sign = if (amount < 0) "-" else ""
        return "$sign₹${String.format(Locale.US, "%,.2f", kotlin.math.abs(amount))}"
    }
}
