package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.InvestmentEntity
import com.moneymind.ai.domain.repository.InvestmentRepository
import javax.inject.Inject

/** Updates an investment's current market value as it changes over time. */
class UpdateInvestmentValueUseCase @Inject constructor(
    private val repository: InvestmentRepository
) {
    suspend operator fun invoke(investment: InvestmentEntity, newCurrentValue: Double) {
        require(newCurrentValue >= 0) { "Current value cannot be negative" }
        repository.updateInvestment(investment.copy(currentValue = newCurrentValue))
    }
}
