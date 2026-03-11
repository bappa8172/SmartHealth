package com.smart.health.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.smart.health.MainActivity
import com.smart.health.R

object NotificationHelper {
    
    const val CHANNEL_ID = "wellness_reminders"
    const val CHANNEL_NAME = "Wellness Reminders"
    const val CHANNEL_DESCRIPTION = "Notifications for health breaks and reminders"
    
    const val ACHIEVEMENT_CHANNEL_ID = "achievements"
    const val ACHIEVEMENT_CHANNEL_NAME = "Achievements"
    const val ACHIEVEMENT_CHANNEL_DESCRIPTION = "Notifications for unlocked achievements"
    
    const val BREAK_NOTIFICATION_ID = 1001
    const val HYDRATION_NOTIFICATION_ID = 1002
    const val STRETCH_NOTIFICATION_ID = 1003
    const val ACHIEVEMENT_NOTIFICATION_ID = 2001
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Wellness reminders channel
            val wellnessChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(wellnessChannel)
            
            // Achievements channel
            val achievementChannel = NotificationChannel(
                ACHIEVEMENT_CHANNEL_ID,
                ACHIEVEMENT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = ACHIEVEMENT_CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(achievementChannel)
        }
    }
    
    fun sendBreakReminder(context: Context, title: String, message: String, notificationId: Int = BREAK_NOTIFICATION_ID) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Handle notification permission not granted
            e.printStackTrace()
        }
    }
    
    fun sendAchievementNotification(context: Context, title: String, description: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, ACHIEVEMENT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle("🏆 Achievement Unlocked!")
            .setContentText(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$title\n$description")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 300, 200, 300, 200, 300))
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(
                ACHIEVEMENT_NOTIFICATION_ID + System.currentTimeMillis().toInt(),
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    
    fun sendHydrationReminder(context: Context) {
        sendBreakReminder(
            context,
            "💧 Time to Hydrate!",
            "Take a moment to drink some water. Stay hydrated, stay healthy!",
            HYDRATION_NOTIFICATION_ID
        )
    }
    
    fun sendStretchReminder(context: Context) {
        sendBreakReminder(
            context,
            "🧘 Stretch Break!",
            "Your body needs a stretch! Take 5 minutes to move around.",
            STRETCH_NOTIFICATION_ID
        )
    }
    
    fun sendBreathingReminder(context: Context) {
        sendBreakReminder(
            context,
            "🫁 Breathing Exercise",
            "Take a deep breath! Try the 4-7-8 breathing technique.",
            BREAK_NOTIFICATION_ID
        )
    }
    
    fun sendCustomReminder(context: Context, routineName: String) {
        sendBreakReminder(
            context,
            "⭐ Wellness Reminder",
            "Time for your $routineName routine!",
            BREAK_NOTIFICATION_ID
        )
    }
}
