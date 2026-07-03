package com.moneymind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val company: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val notes: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)
