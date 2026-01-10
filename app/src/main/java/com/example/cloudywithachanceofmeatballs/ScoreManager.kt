package com.example.cloudywithachanceofmeatballs

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreManager(context: Context) {
    private val database: AppDatabase = AppDatabase.getDatabase(context)

    fun saveScore(playerName: String, distance: Int, lat: Double, lng: Double, onSaved: () -> Unit) {

        val newScore = Score(
            playerName = playerName,
            distance = distance,
            lat = lat,
            lng = lng
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.scoreDao().insertScore(newScore)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ScoreManager", "Error saving score: ${e.message}")
            } finally {
                CoroutineScope(Dispatchers.Main).launch {
                    onSaved()
                }
            }
        }
    }
}