package com.moneymind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.moneymind.ai.data.local.entity.InvoiceEntity

@Dao
interface InvoiceDao {

    @Insert
    suspend fun insert(invoice: InvoiceEntity): Long

    @Update
    suspend fun update(invoice: InvoiceEntity)

    @Delete
    suspend fun delete(invoice: InvoiceEntity)

    @Query("SELECT * FROM invoices ORDER BY issueDateMillis DESC")
    suspend fun getAll(): List<InvoiceEntity>

    @Query("SELECT * FROM invoices WHERE clientId = :clientId ORDER BY issueDateMillis DESC")
    suspend fun getByClient(clientId: Long): List<InvoiceEntity>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM invoices WHERE clientId = :clientId AND status != 'PAID'")
    suspend fun getPendingTotalForClient(clientId: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM invoices WHERE status != 'PAID'")
    suspend fun getTotalPending(): Double
}
