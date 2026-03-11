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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smart.health.R
import com.smart.health.utils.SoundPlayer
import com.smart.health.viewmodel.WellnessViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationScreen(
    navController: NavController,
    viewModel: WellnessViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var totalWaterToday by remember { mutableStateOf(0) }
    var goalReached by remember { mutableStateOf(false) }
    val dailyGoal = 2000
    
    // Play completion sound when goal is reached
    LaunchedEffect(totalWaterToday) {
        if (totalWaterToday >= dailyGoal && !goalReached) {
            goalReached = true
            try {
                SoundPlayer.playCompletionSound(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    var showCustomDialog by remember { mutableStateOf(false) }
    var customAmount by remember { mutableStateOf("250") }
    
    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("Custom Amount") },
            text = {
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it.filter { c -> c.isDigit() } },
                    label = { Text("Amount (ml)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = customAmount.toIntOrNull() ?: 250
                    totalWaterToday += amount
                    showCustomDialog = false
                }) {
                    Text("LOG")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text("CANCEL")
                }
            }
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
                            Text("Hydration Log", fontSize = 12.sp, color = Color.Gray)
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
                                .background(Color(0xFFE1F5FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💧", fontSize = 120.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Pose Name: Hydration Refresh",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text("Description:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(
                                "Drinking water is the simplest way to boost energy and focus. Log your intake now.",
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text("Instructions:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            listOf(
                                "Keep water on your desk within reach.",
                                "When nudge appears, take a steady drink.",
                                "Aim for small, consistent sips throughout the day.",
                                "Notice mental clarity and energy improve."
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
                                .background(Color(0xFFE1F5FE).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Logged Today:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
                                Text(
                                    text = "${totalWaterToday}ml",
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LinearProgressIndicator(
                            progress = { (totalWaterToday.toFloat() / dailyGoal).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF2196F3),
                            trackColor = Color(0xFFE1F5FE)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Daily Goal: ${dailyGoal}ml",
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
                                    totalWaterToday += 250
                                    coroutineScope.launch {
                                        val database = com.smart.health.data.database.WellnessDatabase.getDatabase(context)
                                        val repository = com.smart.health.data.repository.WellnessRepository(database.wellnessDao())
                                        repository.recordCompletedActivity("hydration", "Hydration", 1)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("LOG 250ml", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            
                            OutlinedButton(
                                onClick = { showCustomDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2196F3))
                            ) {
                                Text("CUSTOM AMOUNT", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
