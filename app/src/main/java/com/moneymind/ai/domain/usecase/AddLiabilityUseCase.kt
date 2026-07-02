package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.LiabilityEntity
import com.moneymind.ai.domain.repository.LiabilityRepository
import javax.inject.Inject

class AddLiabilityUseCase @Inject constructor(
    private val repository: LiabilityRepository
) {
    suspend operator fun invoke(liability: LiabilityEntity): Long = repository.addLiability(liability)
}
