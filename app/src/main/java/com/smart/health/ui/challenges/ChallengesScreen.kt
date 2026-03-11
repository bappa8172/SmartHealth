package com.smart.health.ui.challenges

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smart.health.R
import com.smart.health.Screen
import com.smart.health.viewmodel.WellnessViewModel
import kotlinx.coroutines.launch

data class Challenge(
    val id: String,
    val emoji: String,
    val title: String,
    val points: Int,
    val route: Screen,
    val buttonText: String = "Start",
    val iconDrawable: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    navController: NavController,
    viewModel: WellnessViewModel
) {
    val dailyStats by viewModel.dailyStats.collectAsState()
    val completedToday = remember { mutableStateListOf<String>() }
    
    // Define the 6 micro-break challenges
    val challenges = remember {
        listOf(
            Challenge(
                id = "stretch",
                emoji = "🧘",
                title = "Stretch Your Body",
                points = 15,
                route = Screen.Stretch,
                buttonText = "Start"
            ),
            Challenge(
                id = "hydration",
                emoji = "💧",
                title = "Drink Water",
                points = 10,
                route = Screen.Hydration,
                buttonText = "Log"
            ),
            Challenge(
                id = "breathing",
                emoji = "🫁",
                title = "Take Deep Breaths",
                points = 15,
                route = Screen.Breathing,
                buttonText = "Start"
            ),
            Challenge(
                id = "spinal_twist",
                emoji = "🔄",
                title = "Spinal Twist",
                points = 10,
                route = Screen.SpinalTwist,
                buttonText = "Start",
                iconDrawable = R.drawable.twist
            ),
            Challenge(
                id = "eye_relief",
                emoji = "👁️",
                title = "Eye Relief",
                points = 10,
                route = Screen.EyeRelief,
                buttonText = "Start"
            ),
            Challenge(
                id = "balance",
                emoji = "🧘‍♀️",
                title = "Balance Pose",
                points = 15,
                route = Screen.Balance,
                buttonText = "Start"
            )
        )
    }
    
    // Gradient background colors
    val gradientColors = listOf(
        Color(0xFFE8F5E9),
        Color(0xFF80CBC4)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Daily Challenges",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color(0xFF2E7D32)
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
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Header Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Complete Micro-Break Challenges",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Take quick wellness breaks throughout your day",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "Micro-Break Suggestions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(challenges) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        isCompleted = completedToday.contains(challenge.id),
                        onStartClick = {
                            navController.navigate(challenge.route.route)
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: Challenge,
    isCompleted: Boolean,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                modifier = Modifier.weight(1f)
            ) {
                // Emoji or Image Circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    if (challenge.iconDrawable != null) {
                        Image(
                            painter = painterResource(id = challenge.iconDrawable),
                            contentDescription = challenge.title,
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = challenge.emoji,
                            fontSize = 24.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = challenge.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+${challenge.points} PTS",
                        fontSize = 13.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Action Button
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = challenge.buttonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
