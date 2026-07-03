package com.moneymind.ai.domain.model

/**
 * A transaction candidate extracted from a bank/UPI SMS, before the user has
 * confirmed it. Nothing derived here ever reaches the ledger automatically —
 * it's shown in a review list where the user checks it off first.
 */
data class ParsedSmsTransaction(
    val id: String,
    val sender: String,
    val rawBody: String,
    val dateMillis: Long,
    val amount: Double,
    val type: TransactionType,
    val bankGuess: String?,
    val merchantGuess: String?,
    val referenceGuess: String?,
    val category: TransactionCategory,
    val paymentMode: PaymentMode,
    val confidence: Float,
    val selected: Boolean = true
)
