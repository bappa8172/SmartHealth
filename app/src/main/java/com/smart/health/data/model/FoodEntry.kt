package com.smart.health.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val foodName: String,
    val calories: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val mealType: MealType = MealType.SNACK
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}
