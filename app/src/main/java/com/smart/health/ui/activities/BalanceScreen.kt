package com.smart.health.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smart.health.R
import com.smart.health.service.TimerService
import com.smart.health.ui.components.DurationPickerDialog
import com.smart.health.viewmodel.WellnessViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    navController: NavController,
    viewModel: WellnessViewModel
) {
    val context = LocalContext.current
    
    val timerState by produceState(initialValue = com.smart.health.service.TimerState()) {
        while (true) {
            val service = TimerService.getInstance()
            if (service != null) {
                service.timerState.collect { state ->
                    value = state
                }
            } else {
                value = com.smart.health.service.TimerState()
                delay(500)
            }
        }
    }
    
    var showDurationPicker by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf(1) }
    
    if (showDurationPicker) {
        DurationPickerDialog(
            title = "Set Duration",
            currentDurationMinutes = selectedDuration,
            onDismiss = { showDurationPicker = false },
            onConfirm = { duration ->
                selectedDuration = duration
                showDurationPicker = false
            },
            predefinedDurations = listOf(1, 2, 3, 5, 10)
        )
    }
    
    val gradientColors = listOf(Color(0xFFE8F5E9), Color(0xFF80CBC4))
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Smart Health", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Alertness Boost", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE8F5E9))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(gradientColors))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.balanced),
                                contentDescription = "Balance Pose Exercise",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Pose Name: Single-Leg Alertness",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text("Description:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(
                                "Improves focus and stability. A simple pose for mental clarity.",
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text("Instructions:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            listOf(
                                "Stand clear of your desk in a safe space.",
                                "Shift your weight to your left leg.",
                                "Place right foot on left shin or inner thigh (not on knee).",
                                "Bring your hands together at chest or overhead.",
                                "Focus your gaze on a fixed point ahead.",
                                "Hold for 30 seconds, then switch legs."
                            ).forEach { instruction ->
                                Text(
                                    text = "• $instruction",
                                    fontSize = 14.sp,
                                    color = Color.Black.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TIME LEFT:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                Text(
                                    text = if (timerState.isRunning && timerState.activityName == "Balance Pose") {
                                        formatTime(timerState.remainingSeconds)
                                    } else {
                                        "01:00"
                                    },
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Timer Status: Set 1 minute",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    TimerService.start(context, selectedDuration, "Balance Pose")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("START BALANCE", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            
                            OutlinedButton(
                                onClick = { showDurationPicker = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4CAF50))
                            ) {
                                Text("CUSTOM ALERT", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
