package com.moneymind.ai.data.repository

import com.moneymind.ai.data.local.dao.LiabilityDao
import com.moneymind.ai.data.local.entity.LiabilityEntity
import com.moneymind.ai.data.local.entity.LiabilityPaymentEntity
import com.moneymind.ai.domain.repository.LiabilityRepository
import javax.inject.Inject

class LiabilityRepositoryImpl @Inject constructor(
    private val dao: LiabilityDao
) : LiabilityRepository {
    override suspend fun addLiability(liability: LiabilityEntity): Long = dao.insertLiability(liability)
    override suspend fun updateLiability(liability: LiabilityEntity) = dao.updateLiability(liability)
    override suspend fun deleteLiability(liability: LiabilityEntity) = dao.deleteLiability(liability)
    override suspend fun recordPayment(payment: LiabilityPaymentEntity) {
        dao.insertPayment(payment)
    }
    override suspend fun getAllActive(): List<LiabilityEntity> = dao.getAllActive()
    override suspend fun getLiabilityById(liabilityId: Long): LiabilityEntity? = dao.getLiabilityById(liabilityId)
    override suspend fun getPaymentsForLiability(liabilityId: Long): List<LiabilityPaymentEntity> =
        dao.getPaymentsForLiability(liabilityId)
    override suspend fun getTotalPaid(liabilityId: Long): Double = dao.getTotalPaid(liabilityId)
    override suspend fun getTotalRemaining(): Double = dao.getTotalRemaining()
}
