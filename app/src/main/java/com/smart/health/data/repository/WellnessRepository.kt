package com.smart.health.data.repository

import com.smart.health.data.database.WellnessDao
import com.smart.health.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class WellnessRepository(private val dao: WellnessDao) {
    
    // Routines
    fun getAllActiveRoutines(): Flow<List<WellnessRoutine>> = dao.getAllActiveRoutines()
    
    fun getAllRoutines(): Flow<List<WellnessRoutine>> = dao.getAllRoutines()
    
    suspend fun getRoutineById(id: Int): WellnessRoutine? = dao.getRoutineById(id)
    
    suspend fun insertRoutine(routine: WellnessRoutine): Long = dao.insertRoutine(routine)
    
    suspend fun updateRoutine(routine: WellnessRoutine) = dao.updateRoutine(routine)
    
    suspend fun deleteRoutine(routine: WellnessRoutine) = dao.deleteRoutine(routine)
    
    // Break Sessions
    fun getAllBreakSessions(): Flow<List<BreakSession>> = dao.getAllBreakSessions()
    
    fun getTodayBreakSessions(): Flow<List<BreakSession>> {
        val (startOfDay, endOfDay) = getDayTimeRange()
        return dao.getBreakSessionsForDay(startOfDay, endOfDay)
    }
    
    suspend fun insertBreakSession(session: BreakSession): Long = dao.insertBreakSession(session)
    
    suspend fun updateBreakSession(session: BreakSession) = dao.updateBreakSession(session)
    
    // Record completed activity and earn points
    suspend fun recordCompletedActivity(
        activityType: String,
        activityName: String,
        durationMinutes: Int
    ): Int {
        val now = System.currentTimeMillis()
        val points = calculateActivityPoints(activityType, durationMinutes)
        
        val session = BreakSession(
            activityType = activityType,
            activityName = activityName,
            startTime = now - (durationMinutes * 60 * 1000),
            endTime = now,
            durationMinutes = durationMinutes,
            completed = true,
            pointsEarned = points
        )
        
        insertBreakSession(session)
        return points
    }
    
    private fun calculateActivityPoints(activityType: String, durationMinutes: Int): Int {
        // Base points per activity type
        val basePoints = when (activityType) {
            "work" -> 10
            "stretching" -> 15
            "breathing" -> 15
            "hydration" -> 10
            else -> 5
        }
        
        // Add bonus points for longer sessions
        val durationBonus = (durationMinutes / 5) * 2
        
        return basePoints + durationBonus
    }
    
    // Food Entries
    fun getAllFoodEntries(): Flow<List<FoodEntry>> = dao.getAllFoodEntries()
    
    fun getTodayFoodEntries(): Flow<List<FoodEntry>> {
        val (startOfDay, endOfDay) = getDayTimeRange()
        return dao.getFoodEntriesForDay(startOfDay, endOfDay)
    }
    
    suspend fun getTotalFoodEntriesCount(): Int = dao.getTotalFoodEntriesCount()
    
    suspend fun insertFoodEntry(entry: FoodEntry): Long = dao.insertFoodEntry(entry)
    
    suspend fun deleteFoodEntry(entry: FoodEntry) = dao.deleteFoodEntry(entry)
    
    // Step Entries
    fun getAllStepEntries(): Flow<List<StepEntry>> = dao.getAllStepEntries()
    
    suspend fun getStepEntryForDate(date: String): StepEntry? = dao.getStepEntryForDate(date)
    
    suspend fun getTotalStepsCount(): Int = dao.getTotalStepsCount()
    
    suspend fun insertStepEntry(entry: StepEntry): Long = dao.insertStepEntry(entry)
    
    suspend fun updateStepEntry(entry: StepEntry) = dao.updateStepEntry(entry)
    
    suspend fun addSteps(steps: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        
        val existingEntry = getStepEntryForDate(today)
        if (existingEntry != null) {
            updateStepEntry(existingEntry.copy(steps = existingEntry.steps + steps))
        } else {
            insertStepEntry(StepEntry(steps = steps, date = today))
        }
    }
    
    // Achievements
    fun getAllAchievements(): Flow<List<Achievement>> = dao.getAllAchievements()
    
    fun getUnlockedAchievements(): Flow<List<Achievement>> = dao.getUnlockedAchievements()
    
    suspend fun getAchievement(type: AchievementType, milestone: Int): Achievement? = 
        dao.getAchievement(type, milestone)
    
    suspend fun insertAchievement(achievement: Achievement): Long = dao.insertAchievement(achievement)
    
    suspend fun updateAchievement(achievement: Achievement) = dao.updateAchievement(achievement)
    
    suspend fun getUnlockedAchievementsCount(): Int = dao.getUnlockedAchievementsCount()
    
    // Initialize all achievements
    suspend fun initializeAchievements() {
        AchievementMilestones.milestones.forEach { milestone ->
            val existing = getAchievement(milestone.type, milestone.milestone)
            if (existing == null) {
                insertAchievement(
                    Achievement(
                        type = milestone.type,
                        milestone = milestone.milestone
                    )
                )
            }
        }
    }
    
    // Check and unlock achievements
    suspend fun checkAndUnlockAchievements(): List<AchievementMilestone> {
        val unlockedAchievements = mutableListOf<AchievementMilestone>()
        
        // Get current counts
        val totalFoodEntries = getTotalFoodEntriesCount()
        val totalSteps = getTotalStepsCount()
        val (startOfDay, endOfDay) = getDayTimeRange()
        val totalBreaks = dao.getTodayBreaksCount(startOfDay, endOfDay)
        val totalPoints = dao.getTodayPoints(startOfDay, endOfDay)
        
        // Check each milestone
        AchievementMilestones.milestones.forEach { milestone ->
            val currentCount = when (milestone.type) {
                AchievementType.FOOD_LOGGED -> totalFoodEntries
                AchievementType.STEPS_TAKEN -> totalSteps
                AchievementType.BREAKS_TAKEN -> totalBreaks
                AchievementType.POINTS_EARNED -> totalPoints
            }
            
            if (currentCount >= milestone.milestone) {
                val achievement = getAchievement(milestone.type, milestone.milestone)
                if (achievement != null && !achievement.isUnlocked) {
                    // Unlock the achievement
                    updateAchievement(
                        achievement.copy(
                            isUnlocked = true,
                            unlockedAt = System.currentTimeMillis()
                        )
                    )
                    unlockedAchievements.add(milestone)
                }
            }
        }
        
        return unlockedAchievements
    }
    
    suspend fun getTodayStats(): DailyStats {
        val (startOfDay, endOfDay) = getDayTimeRange()
        
        val breaksCount = dao.getTodayBreaksCount(startOfDay, endOfDay)
        val points = dao.getTodayPoints(startOfDay, endOfDay)
        val totalMinutes = dao.getTodayTotalMinutes(startOfDay, endOfDay)
        val foodCount = dao.getTodayFoodEntriesCount(startOfDay, endOfDay)
        val steps = dao.getTodayStepsCount(startOfDay, endOfDay)
        val streak = calculateDailyStreak()
        
        // Get activity-specific counts
        val stretchingCount = dao.getTodayActivityCount(startOfDay, endOfDay, "stretching")
        val hydrationCount = dao.getTodayActivityCount(startOfDay, endOfDay, "hydration")
        val breathingCount = dao.getTodayActivityCount(startOfDay, endOfDay, "breathing")
        
        return DailyStats(
            date = Date().toString(),
            totalBreaks = breaksCount,
            totalPoints = points,
            totalMinutes = totalMinutes,
            stretchingCount = stretchingCount,
            hydrationCount = hydrationCount,
            breathingCount = breathingCount,
            foodEntriesCount = foodCount,
            stepsCount = steps,
            dailyStreak = streak
        )
    }
    
    private suspend fun calculateDailyStreak(): Int {
        val datesWithBreaks = dao.getDistinctDatesWithBreaks()
        if (datesWithBreaks.isEmpty()) return 0
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        
        // Check if today has breaks
        if (!datesWithBreaks.contains(today)) return 0
        
        var streak = 1
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        
        // Count consecutive days going backwards
        for (i in datesWithBreaks.indices) {
            if (i == 0) continue // Skip today
            
            val expectedDate = dateFormat.format(calendar.time)
            if (datesWithBreaks[i] == expectedDate) {
                streak++
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            } else {
                break
            }
        }
        
        return streak
    }
    
    private fun getDayTimeRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        
        return Pair(startOfDay, endOfDay)
    }
}
