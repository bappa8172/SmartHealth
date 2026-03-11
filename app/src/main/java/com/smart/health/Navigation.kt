package com.smart.health

sealed class Screen(val route: String) {
    object SignIn : Screen("signin")
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Timer : Screen("timer")
    object Task : Screen("task")
    object Dashboard : Screen("dashboard")
    object Routine : Screen("routine")
    object CreateRoutine : Screen("create_routine")
    object Settings : Screen("settings")
    object FoodTracking : Screen("food_tracking")
    object Stretch : Screen("stretch")
    object SpinalTwist : Screen("spinal_twist")
    object Hydration : Screen("hydration")
    object Breathing : Screen("breathing")
    object EyeRelief : Screen("eye_relief")
    object Balance : Screen("balance")
    object Challenges : Screen("challenges")
    object Leaderboard : Screen("leaderboard")
}
