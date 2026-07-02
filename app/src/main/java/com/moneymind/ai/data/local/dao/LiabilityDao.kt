package com.moneymind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.moneymind.ai.data.local.entity.LiabilityEntity
import com.moneymind.ai.data.local.entity.LiabilityPaymentEntity

@Dao
interface LiabilityDao {

    @Insert
    suspend fun insertLiability(liability: LiabilityEntity): Long

    @Update
    suspend fun updateLiability(liability: LiabilityEntity)

    @Delete
    suspend fun deleteLiability(liability: LiabilityEntity)

    @Insert
    suspend fun insertPayment(payment: LiabilityPaymentEntity): Long

    @Query("SELECT * FROM liabilities WHERE isActive = 1 ORDER BY createdAtMillis DESC")
    suspend fun getAllActive(): List<LiabilityEntity>

    @Query("SELECT * FROM liabilities WHERE id = :liabilityId LIMIT 1")
    suspend fun getLiabilityById(liabilityId: Long): LiabilityEntity?

    @Query("SELECT * FROM liability_payments WHERE liabilityId = :liabilityId ORDER BY dateMillis DESC")
    suspend fun getPaymentsForLiability(liabilityId: Long): List<LiabilityPaymentEntity>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM liability_payments WHERE liabilityId = :liabilityId")
    suspend fun getTotalPaid(liabilityId: Long): Double

    /** Sum of (totalAmount - paid) across every active liability — total money owed. */
    @Query(
        """
        SELECT COALESCE(SUM(remaining), 0.0) FROM (
            SELECT l.totalAmount - COALESCE(
                (SELECT SUM(p.amount) FROM liability_payments p WHERE p.liabilityId = l.id), 0.0
            ) AS remaining
            FROM liabilities l
            WHERE l.isActive = 1
        )
        """
    )
    suspend fun getTotalRemaining(): Double
}
