package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.InvestmentEntity
import com.moneymind.ai.domain.repository.InvestmentRepository
import javax.inject.Inject

class AddInvestmentUseCase @Inject constructor(
    private val repository: InvestmentRepository
) {
    suspend operator fun invoke(investment: InvestmentEntity): Long = repository.addInvestment(investment)
}
