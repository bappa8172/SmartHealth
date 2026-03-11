package com.smart.health.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesRepository(private val context: Context) {
    
    companion object {
        private val WORK_DURATION_KEY = intPreferencesKey("work_duration_minutes")
        private val BREAK_DURATION_KEY = intPreferencesKey("break_duration_minutes")
        private val STRETCH_DURATION_KEY = intPreferencesKey("stretch_duration_minutes")
        private val BREATHING_DURATION_KEY = intPreferencesKey("breathing_duration_minutes")
        private val HYDRATION_REMINDER_KEY = intPreferencesKey("hydration_reminder_minutes")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val SOUND_ENABLED_KEY = booleanPreferencesKey("sound_enabled")
        private val VIBRATION_ENABLED_KEY = booleanPreferencesKey("vibration_enabled")
    }
    
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                defaultWorkDurationMinutes = preferences[WORK_DURATION_KEY] ?: 25,
                defaultBreakDurationMinutes = preferences[BREAK_DURATION_KEY] ?: 5,
                defaultStretchDurationMinutes = preferences[STRETCH_DURATION_KEY] ?: 5,
                defaultBreathingDurationMinutes = preferences[BREATHING_DURATION_KEY] ?: 3,
                defaultHydrationReminderMinutes = preferences[HYDRATION_REMINDER_KEY] ?: 60,
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED_KEY] ?: true,
                soundEnabled = preferences[SOUND_ENABLED_KEY] ?: true,
                vibrationEnabled = preferences[VIBRATION_ENABLED_KEY] ?: true
            )
        }
    
    suspend fun updateWorkDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[WORK_DURATION_KEY] = minutes
        }
    }
    
    suspend fun updateBreakDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[BREAK_DURATION_KEY] = minutes
        }
    }
    
    suspend fun updateStretchDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[STRETCH_DURATION_KEY] = minutes
        }
    }
    
    suspend fun updateBreathingDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[BREATHING_DURATION_KEY] = minutes
        }
    }
    
    suspend fun updateHydrationReminder(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[HYDRATION_REMINDER_KEY] = minutes
        }
    }
    
    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }
    
    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED_KEY] = enabled
        }
    }
    
    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED_KEY] = enabled
        }
    }
}
