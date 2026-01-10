package com.example.cloudywithachanceofmeatballs

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "high_scores_table")
data class Score(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var score: Int = 0,
    val playerName: String,
    val distance: Int,
    val lat: Double,
    val lng: Double
)