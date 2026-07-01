package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.LoanEntity
import com.moneymind.ai.domain.repository.LoanRepository
import javax.inject.Inject

class AddLoanUseCase @Inject constructor(
    private val repository: LoanRepository
) {
    suspend operator fun invoke(loan: LoanEntity): Long = repository.addLoan(loan)
}
