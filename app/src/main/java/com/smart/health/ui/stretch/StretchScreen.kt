package com.smart.health.ui.stretch

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smart.health.R
import com.smart.health.service.TimerService
import com.smart.health.ui.components.DurationPickerDialog
import com.smart.health.viewmodel.WellnessViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StretchScreen(
    navController: NavController,
    viewModel: WellnessViewModel
) {
    val context = LocalContext.current
    val userPreferences by viewModel.userPreferences.collectAsState()
    
    // Timer service state
    val timerState by produceState(initialValue = com.smart.health.service.TimerState()) {
        while (true) {
            val service = TimerService.getInstance()
            if (service != null) {
                service.timerState.collect { state ->
                    value = state
                }
            } else {
                value = com.smart.health.service.TimerState()
                kotlinx.coroutines.delay(500)
            }
        }
    }
    
    var showDurationPicker by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf(5) }
    
    LaunchedEffect(userPreferences.defaultStretchDurationMinutes) {
        if (userPreferences.defaultStretchDurationMinutes > 0) {
            selectedDuration = userPreferences.defaultStretchDurationMinutes
        }
    }
    
    if (showDurationPicker) {
        DurationPickerDialog(
            title = "Set Stretch Duration",
            currentDurationMinutes = selectedDuration,
            onDismiss = { showDurationPicker = false },
            onConfirm = { duration ->
                selectedDuration = duration
                viewModel.updateStretchDuration(duration)
                showDurationPicker = false
            },
            predefinedDurations = listOf(1, 3, 5, 10, 15, 20)
        )
    }
    
    val gradientColors = listOf(
        Color(0xFFE8F5E9),
        Color(0xFF80CBC4)
    )
    
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
                        Text(
                            text = "Smart Health",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(gradientColors)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header image - Stretching illustration
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.stretch),
                                contentDescription = "Stretch Exercise",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Pose Name
                        Text(
                            text = "Pose Name: Full Body Stretch",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Description
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Description:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Stand up straight with your feet shoulder-width apart. Reach your arms overhead and interlace your fingers. Gently lean to each side to stretch your torso. Roll your shoulders and neck to release tension.",
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Instructions
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Instructions:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            val instructions = listOf(
                                "Stand up and take a deep breath.",
                                "Reach both arms overhead and stretch upward.",
                                "Interlace your fingers and lean slowly to the right.",
                                "Hold for 10-15 seconds, then lean to the left.",
                                "Roll your shoulders backward 5 times.",
                                "Gently turn your head left and right.",
                                "Take deep breaths throughout."
                            )
                            instructions.forEach { instruction ->
                                Text(
                                    text = "• $instruction",
                                    fontSize = 14.sp,
                                    color = Color.Black.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Timer Display
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "TIME LEFT:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = if (timerState.isRunning && timerState.activityName == "Stretch Break") {
                                        formatTimerDisplay(timerState.remainingSeconds)
                                    } else {
                                        String.format("%02d:%02d", selectedDuration, 0)
                                    },
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Timer Status
                        Text(
                            text = if (timerState.isRunning && timerState.activityName == "Stretch Break") {
                                if (timerState.isPaused) "Timer Status: Paused" else "Timer Status: Running"
                            } else {
                                "Timer Status: Set $selectedDuration minutes"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (!timerState.isRunning) {
                                Button(
                                    onClick = {
                                        TimerService.start(
                                            context,
                                            selectedDuration,
                                            "Stretch Break"
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text(
                                        text = "START STRETCH",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                
                                OutlinedButton(
                                    onClick = { showDurationPicker = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF4CAF50)
                                    )
                                ) {
                                    Text(
                                        text = "CUSTOM TIME",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            } else {
                                if (!timerState.isPaused) {
                                    Button(
                                        onClick = { TimerService.pause(context) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFFA726)
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Text(
                                            text = "PAUSE",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { TimerService.resume(context) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50)
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Text(
                                            text = "RESUME",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                                
                                OutlinedButton(
                                    onClick = { TimerService.stop(context) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFF44336)
                                    )
                                ) {
                                    Text(
                                        text = "STOP",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTimerDisplay(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}
