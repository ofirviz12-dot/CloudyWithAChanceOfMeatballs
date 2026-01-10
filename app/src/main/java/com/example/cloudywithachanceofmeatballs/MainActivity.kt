package com.example.cloudywithachanceofmeatballs

import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.cloudywithachanceofmeatballs.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority


class MainActivity : AppCompatActivity(), GameLogic.GameEventsListener, SensorController.SensorActions {

    private lateinit var binding: ActivityMainBinding

    private lateinit var gameLogic: GameLogic
    private lateinit var uiManager: GameUIManager
    private lateinit var soundManager: SoundManager
    private lateinit var scoreManager: ScoreManager
    private lateinit var vibrationManager: VibrationManager
    private lateinit var sensorController: SensorController
    private lateinit var sensorManager: SensorManager

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var gameTimer: GameTimer
    private var isGameOver = false
    private var isSensorMode = false
    private var currentPlayerName = "Player"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isScoreSaved = false

    private var lastKnownLat: Double? = null
    private var lastKnownLng: Double? = null

    private val LOCATION_PERMISSION_REQUEST = 100

    private lateinit var locationCallback: LocationCallback



    private val gameRunnable = object : Runnable {
        override fun run() {
            if (!isGameOver) {
                gameLogic.updateGameCycle()
                uiManager.updateGrid(gameLogic.itemGridState)
                if (!isGameOver) {
                handler.postDelayed(this, gameLogic.currentSpeed)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        isScoreSaved = false

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    lastKnownLat = location.latitude
                    lastKnownLng = location.longitude
                }
            }
        }
        checkLocationPermission()


        uiManager = GameUIManager(binding)
        soundManager = SoundManager(this)
        scoreManager = ScoreManager(this)
        vibrationManager = VibrationManager(this)
        gameLogic = GameLogic(this)
        sensorController = SensorController(this)

        currentPlayerName = intent.getStringExtra("PLAYER_NAME") ?: "Player"
        uiManager.showToast(this, "Welcome $currentPlayerName")

        startLocationUpdates()

        uiManager.updateHearts(gameLogic.lives)
        uiManager.updatePlayerPosition(gameLogic.playerPos)

        setupGameMode()

        gameTimer = GameTimer { seconds -> binding.distanceText.text = "${seconds}m" }
        gameTimer.startTimer()
    }

    // LifeCycle

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        if (isSensorMode) {
            startSensors()
        }

        if (!isGameOver) {
            startGameLoop()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isSensorMode) stopSensors()
        stopGameLoop()
    }
    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGameLoop()
        if (::gameTimer.isInitialized) gameTimer.stopTimer()
        soundManager.release()
    }

    private fun startGameLoop() {
        stopGameLoop()
        handler.postDelayed(gameRunnable, gameLogic.currentSpeed)
    }

    private fun stopGameLoop() {
        handler.removeCallbacks(gameRunnable)
    }

    private fun startSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(sensorController, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    private fun stopSensors() {
        if (::sensorManager.isInitialized) {
            sensorManager.unregisterListener(sensorController)
        }
    }

    private fun setupGameMode() {
        val gameMode = intent.getStringExtra("GAME_MODE") ?: "SLOW"
        isSensorMode = (gameMode == "SENSORS")

        if (isSensorMode) {
            gameLogic.setSpeed(isFast = false)
            uiManager.updateArrowsVisibility(show = false)
        } else {
            gameLogic.setSpeed(isFast = (gameMode == "FAST"))
            uiManager.updateArrowsVisibility(show = true)

            binding.leftArrow.setOnClickListener { onMoveLeft() }
            binding.rightArrow.setOnClickListener { onMoveRight() }
        }
    }

    override fun onMoveLeft() {
        gameLogic.movePlayerLeft()
        uiManager.updatePlayerPosition(gameLogic.playerPos)
    }

    override fun onMoveRight() {
        gameLogic.movePlayerRight()
        uiManager.updatePlayerPosition(gameLogic.playerPos)
    }

    override fun onSpeedChange(isFast: Boolean) {
        if (isSensorMode) {
            gameLogic.setSpeed(isFast)
        }
    }

    override fun onHit() {
        soundManager.playHitSound()
        vibrationManager.vibrateHit()
        uiManager.showHitEffect()
        uiManager.updateHearts(gameLogic.lives)
        uiManager.showToast(this, "YOU LOSE LIFE")
    }

    override fun onCoinCollected() {
        soundManager.playCoinSound()
        vibrationManager.vibratePickup()
        gameTimer.addBonus(10)
        uiManager.showToast(this, "+10 DISTANCE!")
    }

    private fun saveScoreToDb(lat: Double, lng: Double) {

        if (isScoreSaved) return
        isScoreSaved = true

        scoreManager.saveScore(currentPlayerName, gameTimer.getCurrentTime(), lat, lng) {
            val intent = Intent(this, GameOverActivity::class.java)
            intent.putExtra("DISTANCE", gameTimer.getCurrentTime())
            intent.putExtra("PLAYER_NAME", currentPlayerName)
            startActivity(intent)
            finish()
        }
    }

    override fun onGameOver() {
        isGameOver = true
        handler.removeCallbacksAndMessages(null)
        gameTimer.stopTimer()


        if (isSensorMode) {
            stopSensors()
        }


        val defaultLat = 32.0853
        val defaultLng = 34.7818

        val lat = lastKnownLat ?: defaultLat
        val lng = lastKnownLng ?: defaultLng

        saveScoreToDb(lat, lng)
    }
    private fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L
        )
            .setMinUpdateIntervalMillis(1000L)
            .setMaxUpdateDelayMillis(3000L)
            .build()

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastKnownLat = location.latitude
                lastKnownLng = location.longitude
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    private fun stopLocationUpdates() {
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }



}