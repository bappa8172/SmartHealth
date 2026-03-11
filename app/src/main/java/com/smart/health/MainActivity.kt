package com.smart.health

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.smart.health.ui.theme.SmartHealthTheme
import com.smart.health.utils.NotificationHelper
import com.smart.health.utils.WorkManagerHelper
import com.smart.health.viewmodel.WellnessViewModel

class MainActivity : ComponentActivity() {
    
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, schedule reminders and achievement checker
            WorkManagerHelper.schedulePeriodicReminders(this)
            WorkManagerHelper.scheduleAchievementChecker(this)
        }
    }
    
    private val activityRecognitionPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Activity recognition permission result
        // Timer service can start even if denied, but will use basic foreground service
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Create notification channels
        NotificationHelper.createNotificationChannel(this)
        
        // Request activity recognition permission for Android 10+ (required for health foreground service)
        requestActivityRecognitionPermissionIfNeeded()
        
        // Request notification permission for Android 13+
        requestNotificationPermissionIfNeeded()
        
        setContent {
            SmartHealthTheme {
                val navController = rememberNavController()
                val viewModel: WellnessViewModel = viewModel()
                
                NavigationGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
    
    private fun requestActivityRecognitionPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED -> {
                    // Request permission
                    activityRecognitionPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        }
    }
    
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    WorkManagerHelper.schedulePeriodicReminders(this)
                    WorkManagerHelper.scheduleAchievementChecker(this)
                }
                else -> {
                    // Request permission
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // No permission needed for older versions
            WorkManagerHelper.schedulePeriodicReminders(this)
            WorkManagerHelper.scheduleAchievementChecker(this)
        }
    }
}
