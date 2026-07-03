package com.moneymind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.moneymind.ai.data.local.entity.InvestmentEntity

@Dao
interface InvestmentDao {

    @Insert
    suspend fun insert(investment: InvestmentEntity): Long

    @Update
    suspend fun update(investment: InvestmentEntity)

    @Delete
    suspend fun delete(investment: InvestmentEntity)

    @Query("SELECT * FROM investments WHERE isActive = 1 ORDER BY currentValue DESC")
    suspend fun getAllActive(): List<InvestmentEntity>

    @Query("SELECT COALESCE(SUM(investedAmount), 0.0) FROM investments WHERE isActive = 1")
    suspend fun getTotalInvested(): Double

    @Query("SELECT COALESCE(SUM(currentValue), 0.0) FROM investments WHERE isActive = 1")
    suspend fun getTotalCurrentValue(): Double
}
