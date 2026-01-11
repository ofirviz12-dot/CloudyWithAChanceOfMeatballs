package com.example.cloudywithachanceofmeatballs

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScoreManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("GAME_SCORES", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveScore(name: String, distance: Int, lat: Double, lng: Double, onSaved: () -> Unit) {
        val currentScores = getTopScores().toMutableList()

        currentScores.add(Score(name, distance, lat, lng))

        currentScores.sortByDescending { it.distance }
        val top10 = currentScores.take(10)

        val jsonString = gson.toJson(top10)
        sharedPreferences.edit().putString("SCORES_KEY", jsonString).apply()

        onSaved()
    }

    fun getTopScores(): List<Score> {
        val jsonString = sharedPreferences.getString("SCORES_KEY", null) ?: return emptyList()

        val type = object : TypeToken<List<Score>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}