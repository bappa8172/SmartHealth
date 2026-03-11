package com.smart.health.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wellness_routines")
data class WellnessRoutine(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val breakType: BreakType,
    val durationMinutes: Int,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
