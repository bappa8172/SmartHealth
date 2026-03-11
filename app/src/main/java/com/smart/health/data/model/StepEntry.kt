package com.smart.health.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_entries")
data class StepEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val steps: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String // Format: YYYY-MM-DD
)
