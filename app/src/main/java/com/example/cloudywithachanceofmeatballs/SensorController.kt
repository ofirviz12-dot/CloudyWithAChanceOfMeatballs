package com.example.cloudywithachanceofmeatballs

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class SensorController(private val listener: SensorActions) : SensorEventListener {

    interface SensorActions {
        fun onMoveLeft()
        fun onMoveRight()
        fun onSpeedChange(isFast: Boolean)
    }

    private var lastMovementTime: Long = 0
    private val MOVEMENT_COOLDOWN = 200L
    private val TILT_THRESHOLD = 2.0f


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val xTilt = event.values[0]
            val yTilt = event.values[1]
            val currentTime = System.currentTimeMillis()

            if (yTilt < -TILT_THRESHOLD) {
                listener.onSpeedChange(isFast = true)
            } else if (yTilt > TILT_THRESHOLD) {
                listener.onSpeedChange(isFast = false)
            }

            if (currentTime - lastMovementTime > MOVEMENT_COOLDOWN) {
                if (xTilt > TILT_THRESHOLD) {
                    listener.onMoveLeft()
                    lastMovementTime = currentTime
                } else if (xTilt < -TILT_THRESHOLD) {
                    listener.onMoveRight()
                    lastMovementTime = currentTime
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}