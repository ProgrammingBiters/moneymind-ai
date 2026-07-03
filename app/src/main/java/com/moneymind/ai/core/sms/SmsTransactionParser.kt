package com.moneymind.ai.core.sms

import com.moneymind.ai.domain.model.ParsedSmsTransaction
import com.moneymind.ai.domain.model.PaymentMode
import com.moneymind.ai.domain.model.TransactionCategory
import com.moneymind.ai.domain.model.TransactionType
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Best-effort regex parser for common Indian bank/UPI transaction SMS
 * formats (HDFC, ICICI, SBI, Axis, Kotak, and generic UPI apps like GPay/
 * PhonePe/Paytm all use broadly similar phrasing). This is heuristic, not
 * exhaustive — banks change wording often and this will miss or misparse
 * some messages. That's why every result lands in a review screen with a
 * checkbox, never auto-imported.
 */
@Singleton
class SmsTransactionParser @Inject constructor() {

    private val amountRegex = Regex(
        """(?:rs\.?|inr)\s?([0-9][0-9,]*(?:\.[0-9]{1,2})?)""",
        RegexOption.IGNORE_CASE
    )

    private val debitKeywords = listOf("debited", "spent", "paid", "withdrawn", "sent", "purchase of")
    private val creditKeywords = listOf("credited", "received", "deposited", "refund of")

    private val merchantPatterns = listOf(
        Regex("""(?:at|to)\s+([A-Za-z0-9@.\-_&' ]{3,40})(?:\s+on|\s+via|\.|,|$)""", RegexOption.IGNORE_CASE),
        Regex("""VPA\s+([A-Za-z0-9.\-_@]{3,40})""", RegexOption.IGNORE_CASE)
    )

    private val referencePattern = Regex(
        """(?:ref(?:erence)?\.?\s?(?:no)?\.?|txn\s?id|utr)\s?[:\-]?\s?([A-Za-z0-9]{6,20})""",
        RegexOption.IGNORE_CASE
    )

    private val ignoreKeywords = listOf("otp", "one time password", "will expire", "do not share")

    private val bankSenderMap = mapOf(
        "HDFC" to "HDFC Bank",
        "ICICI" to "ICICI Bank",
        "SBIINB" to "State Bank of India",
        "SBI" to "State Bank of India",
        "AXISBK" to "Axis Bank",
        "KOTAKB" to "Kotak Bank",
        "PAYTM" to "Paytm",
        "GPAY" to "Google Pay",
        "PHONEPE" to "PhonePe",
        "AMEX" to "American Express",
        "YESBNK" to "Yes Bank",
        "PNB" to "Punjab National Bank",
        "IDFC" to "IDFC First Bank"
    )

    /** Returns null if the message doesn't look like a financial transaction at all. */
    fun parse(sms: RawSms): ParsedSmsTransaction? {
        val body = sms.body

        if (ignoreKeywords.any { body.contains(it, ignoreCase = true) }) return null

        val amountMatch = amountRegex.find(body) ?: return null
        val amount = amountMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null
        if (amount <= 0.0) return null

        val isDebit = debitKeywords.any { body.contains(it, ignoreCase = true) }
        val isCredit = creditKeywords.any { body.contains(it, ignoreCase = true) }

        // If both or neither keyword group matched, we can't be confident about direction —
        // still surface it (better to let the user decide than silently drop it), just at low confidence.
        val type = when {
            isCredit && !isDebit -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }

        val merchant = merchantPatterns.firstNotNullOfOrNull { pattern ->
            pattern.find(body)?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() }
        }

        val reference = referencePattern.find(body)?.groupValues?.get(1)

        val bankGuess = bankSenderMap.entries.firstOrNull { (key, _) ->
            sms.sender.contains(key, ignoreCase = true)
        }?.value

        val isUpi = body.contains("UPI", ignoreCase = true) || sms.sender.contains("UPI", ignoreCase = true)
        val paymentMode = when {
            isUpi -> PaymentMode.UPI
            body.contains("credit card", ignoreCase = true) -> PaymentMode.CARD
            body.contains("card", ignoreCase = true) -> PaymentMode.CARD
            else -> PaymentMode.BANK_TRANSFER
        }

        var confidence = 0.5f
        if (isDebit xor isCredit) confidence += 0.25f
        if (merchant != null) confidence += 0.15f
        if (bankGuess != null) confidence += 0.1f

        return ParsedSmsTransaction(
            id = UUID.randomUUID().toString(),
            sender = sms.sender,
            rawBody = body,
            dateMillis = sms.dateMillis,
            amount = amount,
            type = type,
            bankGuess = bankGuess,
            merchantGuess = merchant,
            referenceGuess = reference,
            category = TransactionCategory.OTHERS,
            paymentMode = paymentMode,
            confidence = confidence.coerceIn(0f, 1f)
        )
    }

    fun parseAll(messages: List<RawSms>): List<ParsedSmsTransaction> =
        messages.mapNotNull { parse(it) }
}
