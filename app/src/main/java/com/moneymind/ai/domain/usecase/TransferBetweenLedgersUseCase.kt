package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.PaymentMode
import com.moneymind.ai.domain.model.TransactionCategory
import com.moneymind.ai.domain.model.TransactionType
import com.moneymind.ai.domain.repository.TransactionRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Moves money between the personal and business ledgers without it ever
 * being counted as income or an expense. Implemented as two linked rows
 * (a TRANSFER_OUT in the source ledger, a TRANSFER_IN in the destination)
 * sharing a [TransactionEntity.transferGroupId], so each ledger's balance
 * updates correctly while overall net worth is unaffected — the money never
 * left the person's control, it just changed which book it's tracked in.
 */
class TransferBetweenLedgersUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        amount: Double,
        from: Ledger,
        to: Ledger,
        note: String?
    ) {
        require(amount > 0) { "Transfer amount must be positive" }
        require(from != to) { "Source and destination ledger must differ" }

        val groupId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        repository.addTransaction(
            TransactionEntity(
                amount = amount,
                type = TransactionType.TRANSFER_OUT,
                ledger = from,
                category = TransactionCategory.OTHERS,
                paymentMode = PaymentMode.BANK_TRANSFER,
                note = note ?: "Transfer to ${to.displayName}",
                dateMillis = now,
                transferGroupId = groupId
            )
        )
        repository.addTransaction(
            TransactionEntity(
                amount = amount,
                type = TransactionType.TRANSFER_IN,
                ledger = to,
                category = TransactionCategory.OTHERS,
                paymentMode = PaymentMode.BANK_TRANSFER,
                note = note ?: "Transfer from ${from.displayName}",
                dateMillis = now,
                transferGroupId = groupId
            )
        )
    }
}
