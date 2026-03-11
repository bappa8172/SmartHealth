package com.smart.health.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val totalPoints: Int = 0,
    val dailyStreak: Int = 0,
    val totalBreaks: Int = 0,
    val level: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

data class LeaderboardEntry(
    val uid: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val totalPoints: Int = 0,
    val level: Int = 1,
    val rank: Int = 0
)
