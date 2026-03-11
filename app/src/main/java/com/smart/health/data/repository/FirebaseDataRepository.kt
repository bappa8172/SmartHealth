package com.smart.health.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smart.health.data.model.LeaderboardEntry
import com.smart.health.data.model.User
import kotlinx.coroutines.tasks.await

class FirebaseDataRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    suspend fun getUserData(): Result<User?> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.success(null)
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun updateUserPoints(totalPoints: Int, totalBreaks: Int, dailyStreak: Int): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
            
            val newLevel = calculateLevel(totalPoints)
            
            val updates = hashMapOf<String, Any>(
                "totalPoints" to totalPoints,
                "totalBreaks" to totalBreaks,
                "dailyStreak" to dailyStreak,
                "level" to newLevel,
                "lastUpdated" to System.currentTimeMillis()
            )
            
            firestore.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun getLeaderboard(limit: Int = 50): Result<List<LeaderboardEntry>> {
        return try {
            val snapshot = firestore.collection("users")
                .orderBy("totalPoints", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val leaderboard = snapshot.documents.mapIndexed { index, doc ->
                val user = doc.toObject(User::class.java)
                LeaderboardEntry(
                    uid = user?.uid ?: "",
                    displayName = user?.displayName ?: "Anonymous",
                    photoUrl = user?.photoUrl ?: "",
                    totalPoints = user?.totalPoints ?: 0,
                    level = user?.level ?: 1,
                    rank = index + 1
                )
            }
            
            Result.success(leaderboard)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun getUserRank(): Result<Int> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
            val userDoc = firestore.collection("users").document(uid).get().await()
            val userPoints = userDoc.getLong("totalPoints")?.toInt() ?: 0
            
            val higherUsersCount = firestore.collection("users")
                .whereGreaterThan("totalPoints", userPoints)
                .get()
                .await()
                .size()
            
            Result.success(higherUsersCount + 1)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    private fun calculateLevel(points: Int): Int {
        // Level up every 500 points
        return (points / 500) + 1
    }
}
