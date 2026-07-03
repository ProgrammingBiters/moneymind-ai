package com.moneymind.ai.domain.repository

import com.moneymind.ai.data.local.dao.CategoryTotal
import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.TransactionType

interface TransactionRepository {
    suspend fun addTransaction(transaction: TransactionEntity): Long
    suspend fun updateTransaction(transaction: TransactionEntity)
    suspend fun deleteTransaction(transaction: TransactionEntity)

    /** [ledger] null means "combined across both ledgers". */
    suspend fun getRecent(ledger: Ledger?, limit: Int): List<TransactionEntity>
    suspend fun getInRange(ledger: Ledger?, start: Long, end: Long): List<TransactionEntity>
    suspend fun getSum(ledger: Ledger, type: TransactionType, start: Long, end: Long): Double
    suspend fun getTopExpenseCategories(ledger: Ledger?, start: Long, end: Long, limit: Int): List<CategoryTotal>
}
