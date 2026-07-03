package com.moneymind.ai.domain.repository

import com.moneymind.ai.data.local.entity.LiabilityEntity
import com.moneymind.ai.data.local.entity.LiabilityPaymentEntity

interface LiabilityRepository {
    suspend fun addLiability(liability: LiabilityEntity): Long
    suspend fun updateLiability(liability: LiabilityEntity)
    suspend fun deleteLiability(liability: LiabilityEntity)
    suspend fun recordPayment(payment: LiabilityPaymentEntity)
    suspend fun getAllActive(): List<LiabilityEntity>
    suspend fun getLiabilityById(liabilityId: Long): LiabilityEntity?
    suspend fun getPaymentsForLiability(liabilityId: Long): List<LiabilityPaymentEntity>
    suspend fun getTotalPaid(liabilityId: Long): Double
    suspend fun getTotalRemaining(): Double
}
