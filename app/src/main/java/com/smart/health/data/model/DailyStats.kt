package com.smart.health.data.model

data class DailyStats(
    val date: String,
    val totalBreaks: Int = 0,
    val totalPoints: Int = 0,
    val totalMinutes: Int = 0,
    val stretchingCount: Int = 0,
    val hydrationCount: Int = 0,
    val breathingCount: Int = 0,
    val foodEntriesCount: Int = 0,
    val stepsCount: Int = 0,
    val dailyStreak: Int = 0
)
