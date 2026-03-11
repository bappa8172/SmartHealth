package com.smart.health.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.smart.health.MainActivity
import com.smart.health.R
import com.smart.health.utils.SoundPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerService : Service() {
    
    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private var timerJob: Job? = null
    
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    
    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val COMPLETION_CHANNEL_ID = "timer_completion_channel"
        const val NOTIFICATION_ID = 1001
        
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        
        const val EXTRA_DURATION_MINUTES = "EXTRA_DURATION_MINUTES"
        const val EXTRA_ACTIVITY_NAME = "EXTRA_ACTIVITY_NAME"
        const val EXTRA_ACTIVITY_TYPE = "EXTRA_ACTIVITY_TYPE"
        
        private var instance: TimerService? = null
        
        fun start(
            context: Context, 
            durationMinutes: Int, 
            activityName: String,
            activityType: String = "work"
        ) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DURATION_MINUTES, durationMinutes)
                putExtra(EXTRA_ACTIVITY_NAME, activityName)
                putExtra(EXTRA_ACTIVITY_TYPE, activityType)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun pause(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(intent)
        }
        
        fun resume(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_RESUME
            }
            context.startService(intent)
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
        
        fun getInstance(): TimerService? = instance
    }
    
    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
        
        // Start foreground immediately with a placeholder notification for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val placeholderNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Preparing timer...")
                .setContentText("Starting service")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setSilent(true)
                .build()
            startForeground(NOTIFICATION_ID, placeholderNotification)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Ensure we're in foreground mode
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && _timerState.value.totalSeconds == 0) {
            val placeholderNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Preparing timer...")
                .setContentText("Starting service")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setSilent(true)
                .build()
            startForeground(NOTIFICATION_ID, placeholderNotification)
        }
        
        when (intent?.action) {
            ACTION_START -> {
                val durationMinutes = intent.getIntExtra(EXTRA_DURATION_MINUTES, 25)
                val activityName = intent.getStringExtra(EXTRA_ACTIVITY_NAME) ?: "Focus Time"
                val activityType = intent.getStringExtra(EXTRA_ACTIVITY_TYPE) ?: "work"
                startTimer(durationMinutes, activityName, activityType)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
        }
        
        return START_STICKY
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel for ongoing timer (no sound)
            val timerChannel = NotificationChannel(
                CHANNEL_ID,
                "Timer Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows running timer progress"
                setSound(null, null)
            }
            
            // Channel for completion notifications (with sound)
            val soundUri = try {
                Uri.parse("android.resource://" + packageName + "/" + R.raw.completion_sound)
            } catch (e: Exception) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            
            val completionChannel = NotificationChannel(
                COMPLETION_CHANNEL_ID,
                "Activity Completion",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when activities are completed"
                setSound(soundUri, AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build())
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(timerChannel)
            notificationManager.createNotificationChannel(completionChannel)
        }
    }
    
    private fun startTimer(durationMinutes: Int, activityName: String, activityType: String) {
        try {
            val totalSeconds = durationMinutes * 60
            _timerState.value = TimerState(
                totalSeconds = totalSeconds,
                remainingSeconds = totalSeconds,
                activityName = activityName,
                activityType = activityType,
                durationMinutes = durationMinutes,
                isRunning = true,
                isPaused = false
            )
            
            // Update to actual notification immediately
            val notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)
            
            timerJob?.cancel()
            timerJob = serviceScope.launch {
                while (_timerState.value.isRunning && _timerState.value.remainingSeconds > 0) {
                    delay(1000)
                    if (!_timerState.value.isPaused) {
                        _timerState.value = _timerState.value.copy(
                            remainingSeconds = _timerState.value.remainingSeconds - 1
                        )
                        updateNotification()
                    }
                }
                
                if (_timerState.value.remainingSeconds == 0) {
                    onTimerComplete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _timerState.value = TimerState()
            stopSelf()
        }
    }
    
    private fun pauseTimer() {
        _timerState.value = _timerState.value.copy(isPaused = true)
        updateNotification()
    }
    
    private fun resumeTimer() {
        _timerState.value = _timerState.value.copy(isPaused = false)
        updateNotification()
    }
    
    private fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    private fun onTimerComplete() {
        val currentState = _timerState.value
        _timerState.value = currentState.copy(
            isRunning = false,
            isComplete = true
        )
        
        // Play completion sound for 5 seconds
        try {
            SoundPlayer.playCompletionSound(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Save completion to database
        serviceScope.launch(Dispatchers.IO) {
            try {
                val database = com.smart.health.data.database.WellnessDatabase.getDatabase(applicationContext)
                val repository = com.smart.health.data.repository.WellnessRepository(database.wellnessDao())
                
                val pointsEarned = repository.recordCompletedActivity(
                    activityType = currentState.activityType,
                    activityName = currentState.activityName,
                    durationMinutes = currentState.durationMinutes
                )
                
                // Update state with earned points
                _timerState.value = _timerState.value.copy(pointsEarned = pointsEarned)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Show completion notification
        val completionNotification = createCompletionNotification()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID + 1, completionNotification)
        
        // Stop the service after a delay
        serviceScope.launch {
            delay(5000)  // Give more time to see completion
            stopTimer()
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val pauseIntent = Intent(this, TimerService::class.java).apply {
            action = if (_timerState.value.isPaused) ACTION_RESUME else ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getService(
            this, 1, pauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val stopIntent = Intent(this, TimerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 2, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val pauseResumeText = if (_timerState.value.isPaused) "Resume" else "Pause"
        val remainingTime = formatTime(_timerState.value.remainingSeconds)
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("${_timerState.value.activityName} - $remainingTime")
            .setContentText(if (_timerState.value.isPaused) "Timer Paused" else "Timer Running")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(0, pauseResumeText, pausePendingIntent)
            .addAction(0, "Stop", stopPendingIntent)
            .setSilent(true)
            .setProgress(
                _timerState.value.totalSeconds, 
                _timerState.value.totalSeconds - _timerState.value.remainingSeconds, 
                false
            )
            .build()
    }
    
    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun createCompletionNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Get sound URI
        val soundUri = try {
            Uri.parse("android.resource://" + packageName + "/" + R.raw.completion_sound)
        } catch (e: Exception) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        
        return NotificationCompat.Builder(this, COMPLETION_CHANNEL_ID)
            .setContentTitle("${_timerState.value.activityName} Complete! 🎉")
            .setContentText("Great job! You've completed your ${_timerState.value.activityName.lowercase()}.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .build()
    }
    
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        SoundPlayer.release()
        serviceScope.cancel()
        instance = null
    }
}

data class TimerState(
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val activityName: String = "",
    val activityType: String = "work",
    val durationMinutes: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isComplete: Boolean = false,
    val pointsEarned: Int = 0
)
