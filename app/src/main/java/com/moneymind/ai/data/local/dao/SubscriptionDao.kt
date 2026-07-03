package com.moneymind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.moneymind.ai.data.local.entity.SubscriptionEntity

@Dao
interface SubscriptionDao {

    @Insert
    suspend fun insert(subscription: SubscriptionEntity): Long

    @Update
    suspend fun update(subscription: SubscriptionEntity)

    @Delete
    suspend fun delete(subscription: SubscriptionEntity)

    @Query("SELECT * FROM subscriptions WHERE isActive = 1 ORDER BY nextRenewalMillis ASC")
    suspend fun getAllActive(): List<SubscriptionEntity>

    @Query(
        "SELECT * FROM subscriptions WHERE isActive = 1 AND nextRenewalMillis BETWEEN :start AND :end ORDER BY nextRenewalMillis ASC"
    )
    suspend fun getRenewingBetween(start: Long, end: Long): List<SubscriptionEntity>

    /** Rough combined monthly cost, normalizing weekly/yearly cycles to a 30-day month. */
    @Query(
        """
        SELECT COALESCE(SUM(
            CASE billingCycle
                WHEN 'WEEKLY' THEN amount * 4.345
                WHEN 'YEARLY' THEN amount / 12.0
                ELSE amount
            END
        ), 0.0)
        FROM subscriptions WHERE isActive = 1
        """
    )
    suspend fun getEstimatedMonthlyCost(): Double
}
