package com.smart.health.data.preferences

data class UserPreferences(
    val defaultWorkDurationMinutes: Int = 25,
    val defaultBreakDurationMinutes: Int = 5,
    val defaultStretchDurationMinutes: Int = 5,
    val defaultBreathingDurationMinutes: Int = 3,
    val defaultHydrationReminderMinutes: Int = 60,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)
