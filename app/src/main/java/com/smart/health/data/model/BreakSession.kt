package com.smart.health.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "break_sessions")
data class BreakSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val routineId: Int = 0,
    val activityType: String = "",  // "work", "stretching", "breathing", "hydration"
    val activityName: String = "",  // Display name like "Focus Time", "Stretch Break"
    val startTime: Long,
    val endTime: Long = 0L,
    val durationMinutes: Int = 0,
    val completed: Boolean = false,
    val pointsEarned: Int = 0
)
