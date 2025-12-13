package com.example.cloudywithachanceofmeatballs

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.os.VibrationEffect
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cloudywithachanceofmeatballs.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val meatballsGrid = Array(5) { arrayOfNulls<ImageView>(3) }

    private val playerCols = arrayOfNulls<ImageView>(3)

    private val hearts = arrayOfNulls<ImageView>(3)

    private var playerPos = 1
    private var lives = 3

    private val handler = Handler(Looper.getMainLooper())
    private val random = Random()

    private val meatballExist = Array(5) { BooleanArray(3) }

    private val gameRunnable = object : Runnable {
        override fun run() {
            try {
                moveMeatballsDown()
                spawnNewMeatball()

                if (lives > 0) {
                    handler.postDelayed(this, 600)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler.postDelayed(this, 600)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        updatePlayer()
        updateHearts()
        setupControls()

        handler.postDelayed(gameRunnable, 800)
    }

    private fun initViews() {

        // row 0
        meatballsGrid[0][0] = binding.cell00
        meatballsGrid[0][1] = binding.cell01
        meatballsGrid[0][2] = binding.cell02

        // row 1
        meatballsGrid[1][0] = binding.cell10
        meatballsGrid[1][1] = binding.cell11
        meatballsGrid[1][2] = binding.cell12

        // row 2
        meatballsGrid[2][0] = binding.cell20
        meatballsGrid[2][1] = binding.cell21
        meatballsGrid[2][2] = binding.cell22

        // row 3
        meatballsGrid[3][0] = binding.cell30
        meatballsGrid[3][1] = binding.cell31
        meatballsGrid[3][2] = binding.cell32

        // row 4
        meatballsGrid[4][0] = binding.cell40
        meatballsGrid[4][1] = binding.cell41
        meatballsGrid[4][2] = binding.cell42


        //player row
        playerCols[0] = binding.leftCol
        playerCols[1] = binding.flint
        playerCols[2] = binding.rightCol
    }

    private fun setupControls() {
        binding.leftArrow.setOnClickListener {
            movePlayerLeft()
        }
        binding.rightArrow.setOnClickListener {
            movePlayerRight()
        }
    }

    private fun movePlayerLeft() {
        if (playerPos > 0) {
            playerPos--
            updatePlayer()
        }
    }

    private fun movePlayerRight() {
        if (playerPos < 2) {
            playerPos++
            updatePlayer()
        }
    }

    private fun updatePlayer() {
        for (i in 0..2) {
            if (i == playerPos) {
                playerCols[i]?.setImageResource(R.drawable.flint)
            } else {
                playerCols[i]?.setImageResource(android.R.color.transparent)
            }
        }
    }

    private fun updateHearts() {
        binding.heart1.visibility = if (lives >= 1) View.VISIBLE else View.INVISIBLE
        binding.heart2.visibility = if (lives >= 2) View.VISIBLE else View.INVISIBLE
        binding.heart3.visibility = if (lives >= 3) View.VISIBLE else View.INVISIBLE
    }


    private fun spawnNewMeatball() {
        val freeCols = mutableListOf<Int>()

        for (col in 0..2) {
            var hasMeatball = false
            for (row in 0..4) {
                if (meatballExist[row][col]) {
                    hasMeatball = true
                    break
                }
            }
            if (!hasMeatball) freeCols.add(col)
        }

        if (freeCols.isNotEmpty()) {
            val col = freeCols[random.nextInt(freeCols.size)]
            meatballExist[0][col] = true
            meatballsGrid[0][col]?.setImageResource(R.drawable.meatball)
        }
    }

    private fun moveMeatballsDown() {
        for (col in 0..2) {

            if (meatballExist[4][col] && col == playerPos) {
                handlePlayerHit()
            }
            if (meatballExist[4][col]) {
                meatballsGrid[4][col]?.setImageResource(android.R.color.transparent)
                meatballExist[4][col] = false
            }
        }
        for (row in 3 downTo 0) {
            for (col in 0..2) {
                if (meatballExist[row][col]) {
                    meatballsGrid[row][col]?.setImageResource(android.R.color.transparent)
                    meatballExist[row][col] = false

                    meatballsGrid[row + 1][col]?.setImageResource(R.drawable.meatball)
                    meatballExist[row + 1][col] = true
                }
            }
        }
    }

    private fun handlePlayerHit() {

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator?

        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        200,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(200)
            }
        }

        Toast.makeText(this, "YOU LOSE LIFE", Toast.LENGTH_SHORT).show()

        lives--
        updateHearts()

        binding.flint.alpha = 0.3f
        handler.postDelayed({
            binding.flint.alpha = 1f
        }, 300)

        if (lives == 0) {
            Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show()
            handler.removeCallbacks(gameRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(gameRunnable)
    }
}