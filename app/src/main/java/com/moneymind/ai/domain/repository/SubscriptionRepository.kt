package com.moneymind.ai.domain.repository

import com.moneymind.ai.data.local.entity.SubscriptionEntity

interface SubscriptionRepository {
    suspend fun addSubscription(subscription: SubscriptionEntity): Long
    suspend fun updateSubscription(subscription: SubscriptionEntity)
    suspend fun deleteSubscription(subscription: SubscriptionEntity)
    suspend fun getAllActive(): List<SubscriptionEntity>
    suspend fun getRenewingBetween(start: Long, end: Long): List<SubscriptionEntity>
    suspend fun getEstimatedMonthlyCost(): Double
}
