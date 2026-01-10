package com.example.cloudywithachanceofmeatballs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cloudywithachanceofmeatballs.databinding.ActivityHighScoreBinding
import com.example.cloudywithachanceofmeatballs.MapFragment
class HighScoreActivity : AppCompatActivity(), OnScoreClickListener {

    private lateinit var binding: ActivityHighScoreBinding
    private var mapFragment: MapFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHighScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        val listFragment = ListFragment()
        listFragment.listener = this

        supportFragmentManager.beginTransaction()
            .replace(binding.listFragmentContainer.id, listFragment)
            .commit()

        mapFragment = MapFragment()
        supportFragmentManager.beginTransaction()
            .replace(binding.mapFragmentContainer.id, mapFragment!!)
            .commit()

    }

    override fun onScoreClick(lat: Double, lng: Double) {
        mapFragment?.zoomToLocation(lat, lng)
    }
}