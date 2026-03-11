package com.smart.health.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smart.health.data.database.WellnessDatabase
import com.smart.health.data.repository.WellnessRepository
import com.smart.health.utils.NotificationHelper

class AchievementWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val database = WellnessDatabase.getDatabase(applicationContext)
            val repository = WellnessRepository(database.wellnessDao())
            
            // Check and unlock achievements
            val newAchievements = repository.checkAndUnlockAchievements()
            
            // Send notifications for newly unlocked achievements
            newAchievements.forEach { achievement ->
                NotificationHelper.sendAchievementNotification(
                    applicationContext,
                    achievement.title,
                    achievement.description
                )
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
    
    companion object {
        const val WORK_NAME = "achievement_checker"
    }
}
