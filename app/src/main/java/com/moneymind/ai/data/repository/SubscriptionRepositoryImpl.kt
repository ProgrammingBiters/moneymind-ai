package com.moneymind.ai.data.repository

import com.moneymind.ai.data.local.dao.SubscriptionDao
import com.moneymind.ai.data.local.entity.SubscriptionEntity
import com.moneymind.ai.domain.repository.SubscriptionRepository
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val dao: SubscriptionDao
) : SubscriptionRepository {
    override suspend fun addSubscription(subscription: SubscriptionEntity): Long = dao.insert(subscription)
    override suspend fun updateSubscription(subscription: SubscriptionEntity) = dao.update(subscription)
    override suspend fun deleteSubscription(subscription: SubscriptionEntity) = dao.delete(subscription)
    override suspend fun getAllActive(): List<SubscriptionEntity> = dao.getAllActive()
    override suspend fun getRenewingBetween(start: Long, end: Long): List<SubscriptionEntity> =
        dao.getRenewingBetween(start, end)
    override suspend fun getEstimatedMonthlyCost(): Double = dao.getEstimatedMonthlyCost()
}
