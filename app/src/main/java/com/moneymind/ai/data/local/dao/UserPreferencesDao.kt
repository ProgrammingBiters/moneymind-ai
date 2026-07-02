package com.moneymind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.moneymind.ai.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {

    @Query("SELECT * FROM user_preferences WHERE id = 0 LIMIT 1")
    fun observe(): Flow<UserPreferencesEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = 0 LIMIT 1")
    suspend fun get(): UserPreferencesEntity?

    @Upsert
    suspend fun upsert(prefs: UserPreferencesEntity)
}
