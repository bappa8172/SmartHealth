package com.smart.health.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smart.health.data.model.*

@Database(
    entities = [
        WellnessRoutine::class,
        BreakSession::class,
        FoodEntry::class,
        StepEntry::class,
        Achievement::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WellnessDatabase : RoomDatabase() {
    
    abstract fun wellnessDao(): WellnessDao
    
    companion object {
        @Volatile
        private var INSTANCE: WellnessDatabase? = null
        
        fun getDatabase(context: Context): WellnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WellnessDatabase::class.java,
                    "wellness_database"
                )
                .fallbackToDestructiveMigration() // For development - removes old data
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
