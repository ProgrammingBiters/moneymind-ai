package com.moneymind.ai.domain.model

enum class PaymentMode(val displayName: String) {
    CASH("Cash"),
    BANK_TRANSFER("Bank Transfer"),
    UPI("UPI"),
    CARD("Card"),
    CHEQUE("Cheque"),
    OTHER("Other")
}
