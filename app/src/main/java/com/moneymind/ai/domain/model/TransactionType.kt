package com.moneymind.ai.domain.model

/**
 * INCOME/EXPENSE are real money movements and count toward income/expense
 * totals. TRANSFER_OUT/TRANSFER_IN are the two halves of a ledger-to-ledger
 * transfer (see [com.moneymind.ai.domain.usecase.TransferBetweenLedgersUseCase])
 * — they affect each ledger's balance but are deliberately excluded from
 * income/expense totals, since moving your own money isn't income or spend.
 */
enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER_IN,
    TRANSFER_OUT
}
