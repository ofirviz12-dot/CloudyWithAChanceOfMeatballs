package com.example.cloudywithachanceofmeatballs

import android.os.Handler
import android.os.Looper

class GameTimer(private val onTick: (Int) -> Unit) {

    private var secondsCounter = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private val timerRunnable = object : Runnable {
        override fun run() {
            secondsCounter++
            onTick(secondsCounter)
            handler.postDelayed(this, 1000)
        }
    }

    fun startTimer() {
        if (!isRunning) {
            secondsCounter = 0
            handler.post(timerRunnable)
            isRunning = true
        }
    }
    fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
        isRunning = false
    }

    fun getCurrentTime(): Int {
        return secondsCounter
    }
    fun addBonus(amount: Int) {
        secondsCounter += amount
        onTick(secondsCounter)
    }
}