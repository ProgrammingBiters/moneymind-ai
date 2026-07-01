package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.LoanPaymentEntity
import com.moneymind.ai.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Records a repayment against a loan and auto-marks the loan settled once
 * the total repaid meets or exceeds the principal — so the list stops
 * nagging about a loan that's actually been paid back in full.
 */
class RecordLoanPaymentUseCase @Inject constructor(
    private val repository: LoanRepository
) {
    suspend operator fun invoke(loanId: Long, amount: Double, note: String?) {
        require(amount > 0) { "Payment amount must be positive" }
        repository.recordPayment(LoanPaymentEntity(loanId = loanId, amount = amount, note = note))

        val loan = repository.getLoanById(loanId) ?: return
        val totalRepaid = repository.getTotalRepaid(loanId)
        if (totalRepaid >= loan.principalAmount && !loan.isSettled) {
            repository.updateLoan(loan.copy(isSettled = true))
        }
    }
}
