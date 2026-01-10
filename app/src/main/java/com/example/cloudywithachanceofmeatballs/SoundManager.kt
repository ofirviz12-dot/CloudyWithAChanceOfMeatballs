package com.example.cloudywithachanceofmeatballs

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val soundCoinId: Int
    private val soundCrushId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        soundCoinId = soundPool.load(context, R.raw.coin_pickup, 1)
        soundCrushId = soundPool.load(context, R.raw.hit, 1)
    }

    fun playCoinSound() {
        soundPool.play(soundCoinId, 1f, 1f, 0, 0, 1f)
    }

    fun playHitSound() {
        soundPool.play(soundCrushId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}