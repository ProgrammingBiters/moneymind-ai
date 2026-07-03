package com.moneymind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.moneymind.ai.data.local.entity.LoanEntity
import com.moneymind.ai.data.local.entity.LoanPaymentEntity

/** A loan row with its repayment total already computed, for list display. */
data class LoanWithOutstanding(
    val loan: LoanEntity,
    val totalRepaid: Double
)

@Dao
interface LoanDao {

    @Insert
    suspend fun insertLoan(loan: LoanEntity): Long

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)

    @Insert
    suspend fun insertPayment(payment: LoanPaymentEntity): Long

    @Query("SELECT * FROM loans ORDER BY isSettled ASC, createdAtMillis DESC")
    suspend fun getAllLoans(): List<LoanEntity>

    @Query("SELECT * FROM loans WHERE id = :loanId LIMIT 1")
    suspend fun getLoanById(loanId: Long): LoanEntity?

    @Query("SELECT * FROM loan_payments WHERE loanId = :loanId ORDER BY dateMillis DESC")
    suspend fun getPaymentsForLoan(loanId: Long): List<LoanPaymentEntity>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM loan_payments WHERE loanId = :loanId")
    suspend fun getTotalRepaid(loanId: Long): Double

    /**
     * Sum of (principal - repaid) across every unsettled GIVEN loan — i.e.
     * "pending money to receive", one of the core dashboard numbers.
     */
    @Query(
        """
        SELECT COALESCE(SUM(outstanding), 0.0) FROM (
            SELECT l.principalAmount - COALESCE(
                (SELECT SUM(p.amount) FROM loan_payments p WHERE p.loanId = l.id), 0.0
            ) AS outstanding
            FROM loans l
            WHERE l.direction = 'GIVEN' AND l.isSettled = 0
        )
        """
    )
    suspend fun getTotalOutstandingGiven(): Double
}
