package com.moneymind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.moneymind.ai.data.local.entity.TransactionEntity
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.TransactionCategory
import com.moneymind.ai.domain.model.TransactionType

/** Projection row for a "spending by category" breakdown query. */
data class CategoryTotal(
    val category: TransactionCategory,
    val total: Double
)

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC LIMIT :limit")
    suspend fun getRecentAll(limit: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE ledger = :ledger ORDER BY dateMillis DESC LIMIT :limit")
    suspend fun getRecentByLedger(ledger: Ledger, limit: Int): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE dateMillis BETWEEN :start AND :end ORDER BY dateMillis DESC")
    suspend fun getInRangeAll(start: Long, end: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE ledger = :ledger AND dateMillis BETWEEN :start AND :end ORDER BY dateMillis DESC")
    suspend fun getInRangeByLedger(ledger: Ledger, start: Long, end: Long): List<TransactionEntity>

    /** Sum of amounts for a given ledger + movement type inside [start, end] (both epoch millis, inclusive). */
    @Query(
        """
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions
        WHERE ledger = :ledger AND type = :type AND dateMillis BETWEEN :start AND :end
        """
    )
    suspend fun getSum(ledger: Ledger, type: TransactionType, start: Long, end: Long): Double

    @Query(
        """
        SELECT category, COALESCE(SUM(amount), 0.0) as total FROM transactions
        WHERE ledger = :ledger AND type = 'EXPENSE' AND dateMillis BETWEEN :start AND :end
        GROUP BY category ORDER BY total DESC LIMIT :limit
        """
    )
    suspend fun getTopExpenseCategoriesByLedger(ledger: Ledger, start: Long, end: Long, limit: Int): List<CategoryTotal>

    @Query(
        """
        SELECT category, COALESCE(SUM(amount), 0.0) as total FROM transactions
        WHERE type = 'EXPENSE' AND dateMillis BETWEEN :start AND :end
        GROUP BY category ORDER BY total DESC LIMIT :limit
        """
    )
    suspend fun getTopExpenseCategoriesAll(start: Long, end: Long, limit: Int): List<CategoryTotal>
}
