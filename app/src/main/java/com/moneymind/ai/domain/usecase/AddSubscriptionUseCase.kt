package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.SubscriptionEntity
import com.moneymind.ai.domain.repository.SubscriptionRepository
import javax.inject.Inject

class AddSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(subscription: SubscriptionEntity): Long =
        repository.addSubscription(subscription)
}
