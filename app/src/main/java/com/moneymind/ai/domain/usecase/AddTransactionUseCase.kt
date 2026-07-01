package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: TransactionEntity): Long =
        repository.addTransaction(transaction)
}
