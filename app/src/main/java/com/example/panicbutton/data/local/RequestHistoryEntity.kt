package com.example.panicbutton.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request_history")
data class RequestHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val helperName: String,
    val distanceKm: Double,
    val outcome: String
)
