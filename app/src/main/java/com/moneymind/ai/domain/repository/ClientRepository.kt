package com.moneymind.ai.domain.repository

import com.moneymind.ai.data.local.entity.ClientEntity

interface ClientRepository {
    suspend fun addClient(client: ClientEntity): Long
    suspend fun updateClient(client: ClientEntity)
    suspend fun deleteClient(client: ClientEntity)
    suspend fun getAllClients(): List<ClientEntity>
    suspend fun getClientById(clientId: Long): ClientEntity?
}
