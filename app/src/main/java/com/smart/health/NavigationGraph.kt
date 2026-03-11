package com.smart.health

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smart.health.ui.splash.SplashScreen
import com.smart.health.ui.home.HomeScreen
import com.smart.health.ui.timer.TimerScreen
import com.smart.health.ui.dashboard.DashboardScreen
import com.smart.health.ui.routine.RoutineScreen
import com.smart.health.ui.routine.CreateRoutineScreen
import com.smart.health.ui.task.TaskScreen
import com.smart.health.ui.settings.SettingsScreen
import com.smart.health.ui.food.FoodTrackingScreen
import com.smart.health.ui.stretch.StretchScreen
import com.smart.health.ui.activities.*
import com.smart.health.ui.challenges.ChallengesScreen
import com.smart.health.ui.auth.SignInScreen
import com.smart.health.ui.leaderboard.LeaderboardScreen
import com.smart.health.viewmodel.WellnessViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: WellnessViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Screen.Home.route) {
                        // Remove splash from backstack so back button doesn't go to splash
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Timer.route) {
            TimerScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Task.route) {
            TaskScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Routine.route) {
            RoutineScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.CreateRoutine.route) {
            CreateRoutineScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.FoodTracking.route) {
            FoodTrackingScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Stretch.route) {
            StretchScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.SpinalTwist.route) {
            SpinalTwistScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Hydration.route) {
            HydrationScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Breathing.route) {
            BreathingScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.EyeRelief.route) {
            EyeReliefScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Balance.route) {
            BalanceScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Challenges.route) {
            ChallengesScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
