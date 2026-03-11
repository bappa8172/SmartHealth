package com.smart.health.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.CheckCircle
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
import com.smart.health.Screen
import com.smart.health.service.TimerService
import com.smart.health.ui.components.ActivityCompletionDialog
import com.smart.health.ui.components.DurationPickerDialog
import com.smart.health.viewmodel.WellnessViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WellnessViewModel
) {
    val context = LocalContext.current
    val dailyStats by viewModel.dailyStats.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()
    val dailyStreak = dailyStats.dailyStreak
    val microBreaksCompleted = dailyStats.totalBreaks
    val microBreaksTarget = 10
    
    // Timer service state - dynamically check for service instance
    val timerState by produceState(initialValue = com.smart.health.service.TimerState()) {
        while (true) {
            val service = TimerService.getInstance()
            if (service != null) {
                service.timerState.collect { state ->
                    value = state
                }
            } else {
                // Service not running, reset to default state
                value = com.smart.health.service.TimerState()
                kotlinx.coroutines.delay(500) // Check again after delay
            }
        }
    }
    
    var showDurationPicker by remember { mutableStateOf(false) }
    var workDuration by remember { mutableStateOf(25) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var completedActivityData by remember { 
        mutableStateOf<CompletionData?>(null) 
    }
    val coroutineScope = rememberCoroutineScope()
    
    // Detect timer completion and show dialog
    LaunchedEffect(timerState.isComplete) {
        if (timerState.isComplete && timerState.pointsEarned > 0) {
            completedActivityData = CompletionData(
                activityName = timerState.activityName,
                activityType = timerState.activityType,
                durationMinutes = timerState.durationMinutes,
                pointsEarned = timerState.pointsEarned
            )
            showCompletionDialog = true
            viewModel.refreshStats() // Refresh stats after completion
        }
    }
    
    // Update workDuration when preferences load
    LaunchedEffect(userPreferences.defaultWorkDurationMinutes) {
        if (userPreferences.defaultWorkDurationMinutes > 0) {
            workDuration = userPreferences.defaultWorkDurationMinutes
        }
    }
    
    // Refresh stats when screen is displayed
    LaunchedEffect(Unit) {
        viewModel.refreshStats()
        // Sync local data to Firebase if logged in
        viewModel.syncLocalDataToFirebase()
    }
    
    // Show duration picker dialog
    if (showDurationPicker) {
        DurationPickerDialog(
            title = "Set Work Duration",
            currentDurationMinutes = workDuration,
            onDismiss = { showDurationPicker = false },
            onConfirm = { duration ->
                workDuration = duration
                viewModel.updateWorkDuration(duration)
                showDurationPicker = false
            },
            predefinedDurations = listOf(1, 5, 10, 15, 20, 25, 30, 45, 60, 90)
        )
    }
    
    // Show completion dialog
    if (showCompletionDialog && completedActivityData != null) {
        ActivityCompletionDialog(
            activityName = completedActivityData!!.activityName,
            activityType = completedActivityData!!.activityType,
            durationMinutes = completedActivityData!!.durationMinutes,
            pointsEarned = completedActivityData!!.pointsEarned,
            totalPoints = dailyStats.totalPoints,
            totalBreaks = dailyStats.totalBreaks,
            dailyStreak = dailyStats.dailyStreak,
            onDismiss = {
                showCompletionDialog = false
                completedActivityData = null
            }
        )
    }
    
    // Gradient background colors
    val gradientColors = listOf(
        Color(0xFFE8F5E9),
        Color(0xFF80CBC4)
    )
    
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onItemSelected = { index ->
                    when (index) {
                        1 -> navController.navigate(Screen.Dashboard.route)
                        2 -> navController.navigate(Screen.Challenges.route)
                        3 -> navController.navigate(Screen.Routine.route)
                        4 -> navController.navigate(Screen.Settings.route)
                    }
                }
            )
        }
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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Smart Health Logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Health",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Wellness Score Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            text = "Wellness Score",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Daily Streak
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(70.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = { dailyStreak / 30f },
                                        modifier = Modifier.fillMaxSize(),
                                        color = Color(0xFF4CAF50),
                                        strokeWidth = 6.dp
                                    )
                                    Text(
                                        text = "$dailyStreak",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "DAILY STREAK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "$dailyStreak DAYS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            
                            // Wellness Points
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${dailyStats.totalPoints} 🌟",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = "PTS",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            }
                            
                            // Micro-Break Progress
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "MICRO-BREAK",
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                                Box(
                                    modifier = Modifier.size(70.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = { microBreaksCompleted / microBreaksTarget.toFloat() },
                                        modifier = Modifier.fillMaxSize(),
                                        color = Color(0xFF4CAF50),
                                        strokeWidth = 6.dp
                                    )
                                    Text(
                                        text = "$microBreaksCompleted/$microBreaksTarget",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "MICRO-BREAK",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "ACHIEVED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }
                
                // Leaderboard Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    onClick = { navController.navigate(Screen.Leaderboard.route) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 28.sp
                            )
                            Column {
                                Text(
                                    text = "Global Leaderboard",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "See how you rank",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Leaderboard",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Micro-Break Timer
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "TIME TO MICRO-BREAK",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(
                                modifier = Modifier.size(140.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val progress = if (timerState.isRunning && timerState.totalSeconds > 0) {
                                    timerState.remainingSeconds.toFloat() / timerState.totalSeconds.toFloat()
                                } else {
                                    1f
                                }
                                
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0xFF4CAF50),
                                    strokeWidth = 10.dp
                                )
                                Text(
                                    text = if (timerState.isRunning) {
                                        formatTimerDisplay(timerState.remainingSeconds)
                                    } else {
                                        formatTimerDisplay(workDuration * 60)
                                    },
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (!timerState.isRunning) {
                                    Button(
                                        onClick = { 
                                            if (workDuration > 0) {
                                                try {
                                                    TimerService.start(context, workDuration, "Focus Time", "work")
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50)
                                        ),
                                        enabled = workDuration > 0
                                    ) {
                                        Text(
                                            text = "START (${workDuration}min)",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    
                                    OutlinedButton(
                                        onClick = { showDurationPicker = true },
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = "CHANGE",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4CAF50)
                                        )
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { 
                                            if (timerState.isPaused) {
                                                TimerService.resume(context)
                                            } else {
                                                TimerService.pause(context)
                                            }
                                        },
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = if (timerState.isPaused) "RESUME" else "PAUSE",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4CAF50)
                                        )
                                    }
                                    
                                    OutlinedButton(
                                        onClick = { TimerService.stop(context) },
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = "STOP",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Micro-Break Suggestions
                Text(
                    text = "Micro-Break Suggestions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
                
                MicroBreakCard(
                    icon = "🧘",
                    title = "Stretch Your Body",
                    points = "+15 PTS",
                    buttonText = "Start",
                    onClick = { 
                        navController.navigate(Screen.Stretch.route)
                    }
                )
                
                MicroBreakCard(
                    icon = "💧",
                    title = "Drink Water",
                    points = "+10 PTS",
                    buttonText = "Log",
                    onClick = { 
                        navController.navigate(Screen.Hydration.route)
                    }
                )
                
                MicroBreakCard(
                    icon = "🫁",
                    title = "Take Deep Breaths",
                    points = "+15 PTS",
                    buttonText = "Start",
                    onClick = { 
                        navController.navigate(Screen.Breathing.route)
                    }
                )
                
                MicroBreakCard(
                    icon = "🔄",
                    title = "Spinal Twist",
                    points = "+10 PTS",
                    buttonText = "Start",
                    iconDrawable = R.drawable.twist,
                    onClick = { 
                        navController.navigate(Screen.SpinalTwist.route)
                    }
                )
                
                MicroBreakCard(
                    icon = "👁️",
                    title = "Eye Relief",
                    points = "+10 PTS",
                    buttonText = "Start",
                    onClick = { 
                        navController.navigate(Screen.EyeRelief.route)
                    }
                )
                
                MicroBreakCard(
                    icon = "🧘‍♀️",
                    title = "Balance Pose",
                    points = "+15 PTS",
                    buttonText = "Start",
                    onClick = { 
                        navController.navigate(Screen.Balance.route)
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MicroBreakCard(
    icon: String,
    title: String,
    points: String,
    buttonText: String,
    onClick: () -> Unit,
    iconDrawable: Int? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (iconDrawable != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconDrawable),
                            contentDescription = title,
                            modifier = Modifier.size(28.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                } else {
                    Text(text = icon, fontSize = 32.sp)
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = points,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF4CAF50)
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(Icons.Default.Home, "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF4CAF50),
                selectedTextColor = Color(0xFF4CAF50),
                indicatorColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(Icons.Default.List, "Activity") },
            label = { Text("Activity") }
        )
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            icon = { Icon(Icons.Default.Star, "Challenges") },
            label = { Text("Challenges") }
        )
        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            icon = { Icon(Icons.Default.CheckCircle, "Progress") },
            label = { Text("Progress") }
        )
        NavigationBarItem(
            selected = selectedItem == 4,
            onClick = { onItemSelected(4) },
            icon = { Icon(Icons.Default.Person, "Profile") },
            label = { Text("Profile") }
        )
    }
}

fun formatTimerDisplay(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}

data class CompletionData(
    val activityName: String,
    val activityType: String,
    val durationMinutes: Int,
    val pointsEarned: Int
)
