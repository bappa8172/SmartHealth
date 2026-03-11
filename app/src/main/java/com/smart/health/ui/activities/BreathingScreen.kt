package com.smart.health.ui.activities

import androidx.compose.animation.core.*
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    navController: NavController,
    viewModel: WellnessViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isBreathing by remember { mutableStateOf(false) }
    var currentCycle by remember { mutableStateOf(0) }
    var totalCycles by remember { mutableStateOf(4) }
    var breathPhase by remember { mutableStateOf("Ready") } // Inhale, Hold, Exhale, Ready
    var phaseSeconds by remember { mutableStateOf(0) }
    
    var showCycleDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(isBreathing) {
        if (isBreathing && currentCycle < totalCycles) {
            // Inhale 4 seconds
            breathPhase = "Inhale"
            for (i in 1..4) {
                phaseSeconds = i
                delay(1000)
            }
            
            // Hold 7 seconds
            breathPhase = "Hold"
            for (i in 1..7) {
                phaseSeconds = i
                delay(1000)
            }
            
            // Exhale 8 seconds
            breathPhase = "Exhale"
            for (i in 1..8) {
                phaseSeconds = i
                delay(1000)
            }
            
            currentCycle++
            if (currentCycle >= totalCycles) {
                isBreathing = false
                breathPhase = "Complete"
            }
        }
    }
    
    // Play completion sound when breathing exercise is complete
    LaunchedEffect(breathPhase) {
        if (breathPhase == "Complete") {
            try {
                SoundPlayer.playCompletionSound(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    if (showCycleDialog) {
        AlertDialog(
            onDismissRequest = { showCycleDialog = false },
            title = { Text("Custom Cycles") },
            text = {
                Column {
                    Text("Select number of breathing cycles:")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(2, 4, 6, 8).forEach { cycles ->
                            FilterChip(
                                selected = totalCycles == cycles,
                                onClick = { totalCycles = cycles },
                                label = { Text("$cycles") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCycleDialog = false }) {
                    Text("OK")
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
                            Text("Breathing Technique", fontSize = 12.sp, color = Color.Gray)
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
                            Text(text = "🫁", fontSize = 120.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Pose Name: 4-7-8 Relaxing Breath",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text("Description:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(
                                "A complete tranquilizer for the nervous system. Focus only on the count.",
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text("Instructions:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            listOf(
                                "Sit upright with good posture.",
                                "Exhale completely through your mouth.",
                                "Inhale quietly through nose for 4 seconds.",
                                "Hold your breath for 7 seconds.",
                                "Exhale completely through mouth for 8 seconds."
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
                        
                        // Breathing visualizer
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = breathPhase.uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (breathPhase) {
                                    "Inhale" -> Color(0xFF2196F3)
                                    "Hold" -> Color(0xFFFFA726)
                                    "Exhale" -> Color(0xFF4CAF50)
                                    else -> Color.Gray
                                }
                            )
                            
                            if (isBreathing) {
                                Text(
                                    text = "$phaseSeconds ${if (phaseSeconds == 1) "second" else "seconds"}",
                                    fontSize = 18.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Cycle: ${if (isBreathing) currentCycle + 1 else currentCycle} of $totalCycles",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!isBreathing) {
                                        currentCycle = 0
                                        isBreathing = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(24.dp),
                                enabled = !isBreathing
                            ) {
                                Text("START BREATHING", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            
                            OutlinedButton(
                                onClick = { showCycleDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4CAF50)),
                                enabled = !isBreathing
                            ) {
                                Text("CUSTOM CYCLES", fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        if (isBreathing) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = {
                                    isBreathing = false
                                    breathPhase = "Ready"
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336))
                            ) {
                                Text("STOP", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
