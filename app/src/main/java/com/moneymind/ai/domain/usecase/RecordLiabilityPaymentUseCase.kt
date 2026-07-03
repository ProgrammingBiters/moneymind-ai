package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.LiabilityPaymentEntity
import com.moneymind.ai.domain.repository.LiabilityRepository
import javax.inject.Inject

/**
 * Records a payment against a liability (e.g. an EMI or credit card payment)
 * and auto-marks it inactive once fully paid off.
 */
class RecordLiabilityPaymentUseCase @Inject constructor(
    private val repository: LiabilityRepository
) {
    suspend operator fun invoke(liabilityId: Long, amount: Double, note: String?) {
        require(amount > 0) { "Payment amount must be positive" }
        repository.recordPayment(LiabilityPaymentEntity(liabilityId = liabilityId, amount = amount, note = note))

        val liability = repository.getLiabilityById(liabilityId) ?: return
        val totalPaid = repository.getTotalPaid(liabilityId)
        if (totalPaid >= liability.totalAmount && liability.isActive) {
            repository.updateLiability(liability.copy(isActive = false))
        }
    }
}
