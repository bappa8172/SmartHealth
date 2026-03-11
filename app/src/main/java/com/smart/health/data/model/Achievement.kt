package com.smart.health.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: AchievementType,
    val milestone: Int, // 100, 500, 1000, etc.
    val unlockedAt: Long = 0L,
    val isUnlocked: Boolean = false
)

enum class AchievementType {
    FOOD_LOGGED,    // Number of food entries
    STEPS_TAKEN,    // Total steps
    BREAKS_TAKEN,   // Total breaks
    POINTS_EARNED   // Total points
}

data class AchievementMilestone(
    val type: AchievementType,
    val milestone: Int,
    val title: String,
    val description: String
)

object AchievementMilestones {
    val milestones = listOf(
        // Food Milestones
        AchievementMilestone(
            AchievementType.FOOD_LOGGED, 100,
            "Food Tracker Starter 🍎",
            "Logged 100 food entries"
        ),
        AchievementMilestone(
            AchievementType.FOOD_LOGGED, 500,
            "Nutrition Enthusiast 🥗",
            "Logged 500 food entries"
        ),
        AchievementMilestone(
            AchievementType.FOOD_LOGGED, 1000,
            "Diet Master 👨‍🍳",
            "Logged 1000 food entries"
        ),
        
        // Step Milestones
        AchievementMilestone(
            AchievementType.STEPS_TAKEN, 100,
            "First Steps 👣",
            "Walked 100 steps"
        ),
        AchievementMilestone(
            AchievementType.STEPS_TAKEN, 500,
            "Walking Warrior 🚶",
            "Walked 500 steps"
        ),
        AchievementMilestone(
            AchievementType.STEPS_TAKEN, 1000,
            "Step Champion 🏃",
            "Walked 1000 steps"
        ),
        AchievementMilestone(
            AchievementType.STEPS_TAKEN, 5000,
            "Marathon Walker 🏅",
            "Walked 5000 steps"
        ),
        AchievementMilestone(
            AchievementType.STEPS_TAKEN, 10000,
            "Step Legend 👑",
            "Walked 10,000 steps"
        ),
        
        // Break Milestones
        AchievementMilestone(
            AchievementType.BREAKS_TAKEN, 100,
            "Break Beginner 🌟",
            "Completed 100 wellness breaks"
        ),
        AchievementMilestone(
            AchievementType.BREAKS_TAKEN, 500,
            "Wellness Warrior 💪",
            "Completed 500 wellness breaks"
        ),
        AchievementMilestone(
            AchievementType.BREAKS_TAKEN, 1000,
            "Wellness Master 🧘‍♂️",
            "Completed 1000 wellness breaks"
        ),
        
        // Points Milestones
        AchievementMilestone(
            AchievementType.POINTS_EARNED, 100,
            "Point Collector 💎",
            "Earned 100 points"
        ),
        AchievementMilestone(
            AchievementType.POINTS_EARNED, 500,
            "Point Hoarder 💰",
            "Earned 500 points"
        ),
        AchievementMilestone(
            AchievementType.POINTS_EARNED, 1000,
            "Point Legend 🏆",
            "Earned 1000 points"
        )
    )
}
