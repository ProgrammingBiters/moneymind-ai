package com.moneymind.ai.data.repository

import com.moneymind.ai.data.local.dao.LoanDao
import com.moneymind.ai.data.local.entity.LoanEntity
import com.moneymind.ai.data.local.entity.LoanPaymentEntity
import com.moneymind.ai.domain.repository.LoanRepository
import javax.inject.Inject

class LoanRepositoryImpl @Inject constructor(
    private val dao: LoanDao
) : LoanRepository {
    override suspend fun addLoan(loan: LoanEntity): Long = dao.insertLoan(loan)
    override suspend fun updateLoan(loan: LoanEntity) = dao.updateLoan(loan)
    override suspend fun deleteLoan(loan: LoanEntity) = dao.deleteLoan(loan)
    override suspend fun recordPayment(payment: LoanPaymentEntity) {
        dao.insertPayment(payment)
    }
    override suspend fun getAllLoans(): List<LoanEntity> = dao.getAllLoans()
    override suspend fun getLoanById(loanId: Long): LoanEntity? = dao.getLoanById(loanId)
    override suspend fun getPaymentsForLoan(loanId: Long): List<LoanPaymentEntity> = dao.getPaymentsForLoan(loanId)
    override suspend fun getTotalRepaid(loanId: Long): Double = dao.getTotalRepaid(loanId)
    override suspend fun getTotalOutstandingGiven(): Double = dao.getTotalOutstandingGiven()
}
