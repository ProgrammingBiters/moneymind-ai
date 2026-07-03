package com.moneymind.ai.domain.usecase

import com.moneymind.ai.data.local.entity.ClientEntity
import com.moneymind.ai.domain.repository.ClientRepository
import javax.inject.Inject

class AddClientUseCase @Inject constructor(
    private val repository: ClientRepository
) {
    suspend operator fun invoke(client: ClientEntity): Long = repository.addClient(client)
}
