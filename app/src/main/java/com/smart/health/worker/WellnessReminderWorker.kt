package com.smart.health.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smart.health.data.model.BreakType
import com.smart.health.utils.NotificationHelper
import kotlin.random.Random

class WellnessReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Get reminder type from input data
            val reminderType = inputData.getString(KEY_REMINDER_TYPE) ?: return Result.failure()
            
            when (reminderType) {
                BreakType.HYDRATION.name -> {
                    NotificationHelper.sendHydrationReminder(applicationContext)
                }
                BreakType.STRETCHING.name -> {
                    NotificationHelper.sendStretchReminder(applicationContext)
                }
                BreakType.BREATHING.name -> {
                    NotificationHelper.sendBreathingReminder(applicationContext)
                }
                "RANDOM" -> {
                    // Send a random wellness reminder
                    sendRandomReminder()
                }
                else -> {
                    NotificationHelper.sendBreakReminder(
                        applicationContext,
                        "Wellness Reminder",
                        "Time to take a healthy break!"
                    )
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
    
    private fun sendRandomReminder() {
        val reminders = listOf(
            BreakType.HYDRATION,
            BreakType.STRETCHING,
            BreakType.BREATHING
        )
        
        when (reminders.random()) {
            BreakType.HYDRATION -> NotificationHelper.sendHydrationReminder(applicationContext)
            BreakType.STRETCHING -> NotificationHelper.sendStretchReminder(applicationContext)
            BreakType.BREATHING -> NotificationHelper.sendBreathingReminder(applicationContext)
            else -> NotificationHelper.sendBreakReminder(applicationContext, "Break Time!", "Take a wellness break!")
        }
    }
    
    companion object {
        const val KEY_REMINDER_TYPE = "reminder_type"
        const val WORK_NAME_PERIODIC = "wellness_periodic_reminder"
        const val WORK_NAME_ONE_TIME = "wellness_one_time_reminder"
    }
}
