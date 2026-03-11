package com.smart.health.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smart.health.data.model.BreakSession
import com.smart.health.utils.NotificationHelper
import com.smart.health.viewmodel.WellnessViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    navController: NavController,
    viewModel: WellnessViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var timeRemaining by remember { mutableStateOf(25 * 60) } // 25 minutes in seconds
    var isRunning by remember { mutableStateOf(false) }
    var isBreakTime by remember { mutableStateOf(false) }
    var sessionStartTime by remember { mutableStateOf(0L) }
    
    LaunchedEffect(isRunning) {
        while (isRunning && timeRemaining > 0) {
            delay(1000L)
            timeRemaining--
            
            if (timeRemaining == 0) {
                isRunning = false
                
                if (!isBreakTime) {
                    // Work session completed - show break notification
                    NotificationHelper.sendBreakReminder(
                        context,
                        "Great Work! 🎉",
                        "You've completed a work session. Time for a 5-minute break!"
                    )
                    
                    // Save break session
                    val session = BreakSession(
                        routineId = 0,
                        startTime = sessionStartTime,
                        endTime = System.currentTimeMillis(),
                        completed = true,
                        pointsEarned = 10
                    )
                    scope.launch {
                        viewModel.saveBreakSession(session)
                        viewModel.refreshStats()
                    }
                } else {
                    // Break completed
                    NotificationHelper.sendBreakReminder(
                        context,
                        "Break Over! 💪",
                        "Ready to get back to work? Start another focus session!"
                    )
                }
                
                isBreakTime = !isBreakTime
                timeRemaining = if (isBreakTime) 5 * 60 else 25 * 60
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Work Timer") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isBreakTime) 
                        MaterialTheme.colorScheme.tertiaryContainer 
                    else 
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isBreakTime) "Break Time! 🧘" else "Focus Time 💼",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = formatTime(timeRemaining),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { 
                                isRunning = !isRunning
                                if (isRunning && sessionStartTime == 0L) {
                                    sessionStartTime = System.currentTimeMillis()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isRunning) "Pause" else "Start")
                        }
                        
                        OutlinedButton(
                            onClick = { 
                                timeRemaining = if (isBreakTime) 5 * 60 else 25 * 60
                                isRunning = false
                                sessionStartTime = 0L
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "⏰ Pomodoro Timer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Work for 25 minutes, then take a 5-minute break to recharge!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}
