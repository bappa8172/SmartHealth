package com.smart.health.data.database

import androidx.room.TypeConverter
import com.smart.health.data.model.AchievementType
import com.smart.health.data.model.BreakType
import com.smart.health.data.model.MealType

class Converters {
    
    @TypeConverter
    fun fromBreakType(value: BreakType): String {
        return value.name
    }
    
    @TypeConverter
    fun toBreakType(value: String): BreakType {
        return try {
            BreakType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            BreakType.CUSTOM
        }
    }
    
    @TypeConverter
    fun fromMealType(value: MealType): String {
        return value.name
    }
    
    @TypeConverter
    fun toMealType(value: String): MealType {
        return try {
            MealType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            MealType.SNACK
        }
    }
    
    @TypeConverter
    fun fromAchievementType(value: AchievementType): String {
        return value.name
    }
    
    @TypeConverter
    fun toAchievementType(value: String): AchievementType {
        return try {
            AchievementType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            AchievementType.POINTS_EARNED
        }
    }
}
