package com.moneymind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.moneymind.ai.data.local.entity.ClientEntity

@Dao
interface ClientDao {

    @Insert
    suspend fun insert(client: ClientEntity): Long

    @Update
    suspend fun update(client: ClientEntity)

    @Delete
    suspend fun delete(client: ClientEntity)

    @Query("SELECT * FROM clients ORDER BY name ASC")
    suspend fun getAll(): List<ClientEntity>

    @Query("SELECT * FROM clients WHERE id = :clientId LIMIT 1")
    suspend fun getById(clientId: Long): ClientEntity?
}
