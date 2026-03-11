package com.smart.health.utils

import android.content.Context
import androidx.work.*
import com.smart.health.data.model.BreakType
import com.smart.health.worker.AchievementWorker
import com.smart.health.worker.WellnessReminderWorker
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    
    /**
     * Schedule periodic wellness reminders (every 25 minutes - Pomodoro style)
     */
    fun schedulePeriodicReminders(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()
        
        val reminderRequest = PeriodicWorkRequestBuilder<WellnessReminderWorker>(
            25, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES // Flex interval
        )
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    WellnessReminderWorker.KEY_REMINDER_TYPE to "RANDOM"
                )
            )
            .addTag("wellness_reminders")
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WellnessReminderWorker.WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }
    
    /**
     * Schedule periodic achievement checking (every 15 minutes)
     */
    fun scheduleAchievementChecker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()
        
        val achievementRequest = PeriodicWorkRequestBuilder<AchievementWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("achievement_checker")
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            AchievementWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            achievementRequest
        )
    }
    
    /**
     * Trigger achievement check immediately
     */
    fun checkAchievementsNow(context: Context) {
        val achievementRequest = OneTimeWorkRequestBuilder<AchievementWorker>()
            .build()
        
        WorkManager.getInstance(context).enqueue(achievementRequest)
    }
    
    /**
     * Schedule a one-time reminder after specified delay
     */
    fun scheduleOneTimeReminder(
        context: Context,
        delayMinutes: Long,
        breakType: BreakType
    ) {
        val reminderRequest = OneTimeWorkRequestBuilder<WellnessReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(
                workDataOf(
                    WellnessReminderWorker.KEY_REMINDER_TYPE to breakType.name
                )
            )
            .addTag("one_time_reminder")
            .build()
        
        WorkManager.getInstance(context).enqueue(reminderRequest)
    }
    
    /**
     * Cancel all scheduled reminders
     */
    fun cancelAllReminders(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("wellness_reminders")
        WorkManager.getInstance(context).cancelUniqueWork(WellnessReminderWorker.WORK_NAME_PERIODIC)
    }
    
    /**
     * Cancel periodic reminders only
     */
    fun cancelPeriodicReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WellnessReminderWorker.WORK_NAME_PERIODIC)
    }
    
    /**
     * Check if periodic reminders are scheduled
     */
    fun areRemindersScheduled(context: Context, callback: (Boolean) -> Unit) {
        val workManager = WorkManager.getInstance(context)
        val workInfos = workManager.getWorkInfosForUniqueWork(WellnessReminderWorker.WORK_NAME_PERIODIC)
        
        workInfos.addListener({
            val workInfo = workInfos.get().firstOrNull()
            val isScheduled = workInfo?.state == WorkInfo.State.ENQUEUED || 
                            workInfo?.state == WorkInfo.State.RUNNING
            callback(isScheduled)
        }, { it.run() })
    }
    
    /**
     * Schedule hydration reminders every hour
     */
    fun scheduleHydrationReminders(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()
        
        val hydrationRequest = PeriodicWorkRequestBuilder<WellnessReminderWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    WellnessReminderWorker.KEY_REMINDER_TYPE to BreakType.HYDRATION.name
                )
            )
            .addTag("hydration_reminders")
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "hydration_periodic",
            ExistingPeriodicWorkPolicy.KEEP,
            hydrationRequest
        )
    }
}
