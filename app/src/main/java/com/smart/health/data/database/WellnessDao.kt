package com.smart.health.data.database

import androidx.room.*
import com.smart.health.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WellnessDao {
    
    // Routines
    @Query("SELECT * FROM wellness_routines WHERE isActive = 1")
    fun getAllActiveRoutines(): Flow<List<WellnessRoutine>>
    
    @Query("SELECT * FROM wellness_routines")
    fun getAllRoutines(): Flow<List<WellnessRoutine>>
    
    @Query("SELECT * FROM wellness_routines WHERE id = :routineId")
    suspend fun getRoutineById(routineId: Int): WellnessRoutine?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: WellnessRoutine): Long
    
    @Update
    suspend fun updateRoutine(routine: WellnessRoutine)
    
    @Delete
    suspend fun deleteRoutine(routine: WellnessRoutine)
    
    // Break Sessions
    @Query("SELECT * FROM break_sessions ORDER BY startTime DESC")
    fun getAllBreakSessions(): Flow<List<BreakSession>>
    
    @Query("SELECT * FROM break_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay")
    fun getBreakSessionsForDay(startOfDay: Long, endOfDay: Long): Flow<List<BreakSession>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakSession(session: BreakSession): Long
    
    @Update
    suspend fun updateBreakSession(session: BreakSession)
    
    @Query("SELECT COUNT(*) FROM break_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay AND completed = 1")
    suspend fun getTodayBreaksCount(startOfDay: Long, endOfDay: Long): Int
    
    @Query("SELECT COALESCE(SUM(pointsEarned), 0) FROM break_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay AND completed = 1")
    suspend fun getTodayPoints(startOfDay: Long, endOfDay: Long): Int
    
    @Query("SELECT COUNT(*) FROM break_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay AND completed = 1 AND activityType = :activityType")
    suspend fun getTodayActivityCount(startOfDay: Long, endOfDay: Long, activityType: String): Int
    
    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM break_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay AND completed = 1")
    suspend fun getTodayTotalMinutes(startOfDay: Long, endOfDay: Long): Int
    
    // Food Entries
    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllFoodEntries(): Flow<List<FoodEntry>>
    
    @Query("SELECT * FROM food_entries WHERE timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp DESC")
    fun getFoodEntriesForDay(startOfDay: Long, endOfDay: Long): Flow<List<FoodEntry>>
    
    @Query("SELECT COUNT(*) FROM food_entries")
    suspend fun getTotalFoodEntriesCount(): Int
    
    @Query("SELECT COUNT(*) FROM food_entries WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    suspend fun getTodayFoodEntriesCount(startOfDay: Long, endOfDay: Long): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(entry: FoodEntry): Long
    
    @Delete
    suspend fun deleteFoodEntry(entry: FoodEntry)
    
    // Step Entries
    @Query("SELECT * FROM step_entries ORDER BY timestamp DESC")
    fun getAllStepEntries(): Flow<List<StepEntry>>
    
    @Query("SELECT * FROM step_entries WHERE date = :date")
    suspend fun getStepEntryForDate(date: String): StepEntry?
    
    @Query("SELECT COALESCE(SUM(steps), 0) FROM step_entries")
    suspend fun getTotalStepsCount(): Int
    
    @Query("SELECT COALESCE(SUM(steps), 0) FROM step_entries WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    suspend fun getTodayStepsCount(startOfDay: Long, endOfDay: Long): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepEntry(entry: StepEntry): Long
    
    @Update
    suspend fun updateStepEntry(entry: StepEntry)
    
    // Achievements
    @Query("SELECT * FROM achievements ORDER BY milestone ASC")
    fun getAllAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE type = :type AND milestone = :milestone")
    suspend fun getAchievement(type: AchievementType, milestone: Int): Achievement?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement): Long
    
    @Update
    suspend fun updateAchievement(achievement: Achievement)
    
    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    suspend fun getUnlockedAchievementsCount(): Int
    
    // Get all distinct dates with completed breaks (for streak calculation)
    @Query("""SELECT DISTINCT date(startTime/1000, 'unixepoch', 'localtime') as break_date 
              FROM break_sessions 
              WHERE completed = 1 
              ORDER BY break_date DESC""")
    suspend fun getDistinctDatesWithBreaks(): List<String>
}
