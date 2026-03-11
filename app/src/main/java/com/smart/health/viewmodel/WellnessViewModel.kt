package com.smart.health.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smart.health.data.database.WellnessDatabase
import com.smart.health.data.model.*
import com.smart.health.data.preferences.PreferencesRepository
import com.smart.health.data.preferences.UserPreferences
import com.smart.health.data.repository.WellnessRepository
import com.smart.health.data.repository.FirebaseAuthRepository
import com.smart.health.data.repository.FirebaseDataRepository
import com.smart.health.utils.WorkManagerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WellnessViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: WellnessRepository
    private val preferencesRepository: PreferencesRepository = PreferencesRepository(application)
    private val firebaseDataRepository: FirebaseDataRepository = FirebaseDataRepository()
    private val firebaseAuthRepository: FirebaseAuthRepository = FirebaseAuthRepository()
    
    private val _dailyStats = MutableStateFlow(DailyStats(date = ""))
    val dailyStats: StateFlow<DailyStats> = _dailyStats.asStateFlow()
    
    private val _routines = MutableStateFlow<List<WellnessRoutine>>(emptyList())
    val routines: StateFlow<List<WellnessRoutine>> = _routines.asStateFlow()
    
    private val _foodEntries = MutableStateFlow<List<FoodEntry>>(emptyList())
    val foodEntries: StateFlow<List<FoodEntry>> = _foodEntries.asStateFlow()
    
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    
    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())
    
    init {
        val database = WellnessDatabase.getDatabase(application)
        repository = WellnessRepository(database.wellnessDao())
        loadTodayStats()
        loadRoutines()
        loadTodayFoodEntries()
        loadAchievements()
        initializeDefaultRoutines()
        initializeAchievements()
    }
    
    private fun loadTodayStats() {
        viewModelScope.launch {
            _dailyStats.value = repository.getTodayStats()
        }
    }
    
    private fun loadRoutines() {
        viewModelScope.launch {
            repository.getAllActiveRoutines().collect { routineList ->
                _routines.value = routineList
            }
        }
    }
    
    private fun loadTodayFoodEntries() {
        viewModelScope.launch {
            repository.getTodayFoodEntries().collect { entries ->
                _foodEntries.value = entries
            }
        }
    }
    
    private fun loadAchievements() {
        viewModelScope.launch {
            repository.getAllAchievements().collect { achievementList ->
                _achievements.value = achievementList
            }
        }
    }
    
    private fun initializeDefaultRoutines() {
        viewModelScope.launch {
            val existingRoutines = repository.getAllRoutines()
            // Check if we need to add default routines (only on first launch)
            // This is a simple implementation - you can make it more sophisticated
        }
    }
    
    private fun initializeAchievements() {
        viewModelScope.launch {
            repository.initializeAchievements()
        }
    }
    
    fun refreshStats() {
        loadTodayStats()
    }
    
    // Routine methods
    fun addRoutine(routine: WellnessRoutine) {
        viewModelScope.launch {
            repository.insertRoutine(routine)
        }
    }
    
    fun updateRoutine(routine: WellnessRoutine) {
        viewModelScope.launch {
            repository.updateRoutine(routine)
        }
    }
    
    fun deleteRoutine(routine: WellnessRoutine) {
        viewModelScope.launch {
            repository.deleteRoutine(routine)
        }
    }
    
    fun saveBreakSession(session: BreakSession) {
        viewModelScope.launch {
            repository.insertBreakSession(session)
            checkAchievements()
            
            // Sync to Firebase if user is logged in
            if (firebaseAuthRepository.isUserLoggedIn()) {
                val stats = repository.getTodayStats()
                firebaseDataRepository.updateUserPoints(
                    totalPoints = stats.totalPoints,
                    totalBreaks = stats.totalBreaks,
                    dailyStreak = stats.dailyStreak
                )
            }
        }
    }
    
    // Food tracking methods
    fun addFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            repository.insertFoodEntry(foodEntry)
            refreshStats()
            checkAchievements()
            
            // Sync to Firebase if user is logged in
            if (firebaseAuthRepository.isUserLoggedIn()) {
                val stats = repository.getTodayStats()
                firebaseDataRepository.updateUserPoints(
                    totalPoints = stats.totalPoints,
                    totalBreaks = stats.totalBreaks,
                    dailyStreak = stats.dailyStreak
                )
            }
        }
    }
    
    fun deleteFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            repository.deleteFoodEntry(foodEntry)
            refreshStats()
        }
    }
    
    // Step tracking methods
    fun addSteps(steps: Int) {
        viewModelScope.launch {
            repository.addSteps(steps)
            refreshStats()
            checkAchievements()
            
            // Sync to Firebase if user is logged in
            if (firebaseAuthRepository.isUserLoggedIn()) {
                val stats = repository.getTodayStats()
                firebaseDataRepository.updateUserPoints(
                    totalPoints = stats.totalPoints,
                    totalBreaks = stats.totalBreaks,
                    dailyStreak = stats.dailyStreak
                )
            }
        }
    }
    
    // Achievement methods
    fun checkAchievements() {
        viewModelScope.launch {
            val app = getApplication<Application>()
            WorkManagerHelper.checkAchievementsNow(app)
        }
    }
    
    fun getUnlockedAchievements(): List<Achievement> {
        return _achievements.value.filter { it.isUnlocked }
    }
    
    // Preferences methods
    fun updateWorkDuration(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.updateWorkDuration(minutes)
        }
    }
    
    fun updateBreakDuration(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.updateBreakDuration(minutes)
        }
    }
    
    fun updateStretchDuration(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.updateStretchDuration(minutes)
        }
    }
    
    fun updateBreathingDuration(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.updateBreathingDuration(minutes)
        }
    }
    
    fun updateHydrationReminder(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.updateHydrationReminder(minutes)
        }
    }
    
    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateNotificationsEnabled(enabled)
        }
    }
    
    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateSoundEnabled(enabled)
        }
    }
    
    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateVibrationEnabled(enabled)
        }
    }
    
    // Firebase sync method
    fun syncLocalDataToFirebase() {
        viewModelScope.launch {
            if (firebaseAuthRepository.isUserLoggedIn()) {
                val stats = repository.getTodayStats()
                firebaseDataRepository.updateUserPoints(
                    totalPoints = stats.totalPoints,
                    totalBreaks = stats.totalBreaks,
                    dailyStreak = stats.dailyStreak
                )
            }
        }
    }
}
