package com.example.cloudywithachanceofmeatballs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cloudywithachanceofmeatballs.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sp = getSharedPreferences("GameData", Context.MODE_PRIVATE)

        val lastPlayer = sp.getString("last_name", "")
        binding.editPlayerName.setText(lastPlayer)

        binding.btnSlowMood.setOnClickListener {
            startGame("SLOW")
        }

        binding.btnFastMood.setOnClickListener {
            startGame("FAST")
        }

        binding.btnSensors.setOnClickListener {
            startGame("SENSORS")
        }

        binding.btnHighScores.setOnClickListener {
            val intent = Intent(this , HighScoreActivity::class.java)
            startActivity(intent)
        }

    }
    private fun startGame(mode: String) {
        var name = binding.editPlayerName.text.toString().trim()
        if (name.isEmpty()) {
            name = "Guest"
        }
        sp.edit().putString("last_name", name).apply()

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("PLAYER_NAME", name)
            putExtra("GAME_MODE", mode)
        }
        startActivity(intent)
        }
}