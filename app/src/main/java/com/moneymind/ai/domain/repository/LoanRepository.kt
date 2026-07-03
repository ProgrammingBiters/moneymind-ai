package com.moneymind.ai.domain.repository

import com.moneymind.ai.data.local.entity.LoanEntity
import com.moneymind.ai.data.local.entity.LoanPaymentEntity

interface LoanRepository {
    suspend fun addLoan(loan: LoanEntity): Long
    suspend fun updateLoan(loan: LoanEntity)
    suspend fun deleteLoan(loan: LoanEntity)
    suspend fun recordPayment(payment: LoanPaymentEntity)
    suspend fun getAllLoans(): List<LoanEntity>
    suspend fun getLoanById(loanId: Long): LoanEntity?
    suspend fun getPaymentsForLoan(loanId: Long): List<LoanPaymentEntity>
    suspend fun getTotalRepaid(loanId: Long): Double
    suspend fun getTotalOutstandingGiven(): Double
}
