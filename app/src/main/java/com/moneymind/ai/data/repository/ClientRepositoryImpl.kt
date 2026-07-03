package com.moneymind.ai.data.repository

import com.moneymind.ai.data.local.dao.ClientDao
import com.moneymind.ai.data.local.entity.ClientEntity
import com.moneymind.ai.domain.repository.ClientRepository
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val dao: ClientDao
) : ClientRepository {
    override suspend fun addClient(client: ClientEntity): Long = dao.insert(client)
    override suspend fun updateClient(client: ClientEntity) = dao.update(client)
    override suspend fun deleteClient(client: ClientEntity) = dao.delete(client)
    override suspend fun getAllClients(): List<ClientEntity> = dao.getAll()
    override suspend fun getClientById(clientId: Long): ClientEntity? = dao.getById(clientId)
}
