package com.example.cloudywithachanceofmeatballs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    suspend fun insertScore(score: Score)

    @Query("SELECT * FROM high_scores_table ORDER BY distance DESC LIMIT 10")
    suspend fun getTopTenScores(): List<Score>
}