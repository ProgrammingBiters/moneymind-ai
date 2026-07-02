package com.moneymind.ai.data.repository

import com.moneymind.ai.data.local.dao.CategoryTotal
import com.moneymind.ai.data.local.dao.TransactionDao
import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.TransactionType
import com.moneymind.ai.domain.repository.TransactionRepository
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun addTransaction(transaction: TransactionEntity): Long =
        dao.insert(transaction)

    override suspend fun updateTransaction(transaction: TransactionEntity) =
        dao.update(transaction)

    override suspend fun deleteTransaction(transaction: TransactionEntity) =
        dao.delete(transaction)

    override suspend fun getRecent(ledger: Ledger?, limit: Int): List<TransactionEntity> =
        if (ledger == null) dao.getRecentAll(limit) else dao.getRecentByLedger(ledger, limit)

    override suspend fun getSum(ledger: Ledger, type: TransactionType, start: Long, end: Long): Double =
        dao.getSum(ledger, type, start, end)

    override suspend fun getTopExpenseCategories(
        ledger: Ledger?,
        start: Long,
        end: Long,
        limit: Int
    ): List<CategoryTotal> =
        if (ledger == null) dao.getTopExpenseCategoriesAll(start, end, limit)
        else dao.getTopExpenseCategoriesByLedger(ledger, start, end, limit)
}
