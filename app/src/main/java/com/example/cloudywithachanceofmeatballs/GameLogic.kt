package com.example.cloudywithachanceofmeatballs

import java.util.Random

enum class ItemType {
    NONE,
    MEATBALL,
    COIN
}

class GameLogic(private val listener: GameEventsListener) {

    private val rows = 6
    private val cols = 5
    private val random = Random()

    var playerPos = 2
    var lives = 3
    var currentSpeed: Long = 600L

    private var isGameActive = true

    val itemGridState = Array(rows) { Array(cols) { ItemType.NONE } }

    interface GameEventsListener {
        fun onHit()
        fun onCoinCollected()
        fun onGameOver()
    }

    fun setSpeed(isFast: Boolean) {
        currentSpeed = if (isFast) 300L else 600L
    }

    fun movePlayerLeft() {
        if (isGameActive && playerPos > 0) playerPos--
    }

    fun movePlayerRight() {
        if (isGameActive && playerPos < cols - 1) playerPos++
    }

    fun updateGameCycle() {
        moveItemsDown()
        spawnNewItem()

    }

    fun spawnNewItem() {
        val freeCols = mutableListOf<Int>()

        for (col in 0 until cols) {
            var hasItem = false
            for (row in 0 until rows) {
                if (itemGridState[row][col] != ItemType.NONE) {
                    hasItem = true
                    break
                }
            }
            if (!hasItem) freeCols.add(col)
        }

        if (freeCols.isNotEmpty()) {
            val col = freeCols[random.nextInt(freeCols.size)]
            val itemType = if (random.nextInt(10) < 7) ItemType.MEATBALL else ItemType.COIN
            itemGridState[0][col] = itemType
        }
    }

    fun moveItemsDown() {
        for (col in 0 until cols) {
            val itemTypeInLastRow = itemGridState[rows - 1][col]
            if (itemTypeInLastRow != ItemType.NONE) {
                if (col == playerPos) {
                    when (itemTypeInLastRow) {
                        ItemType.MEATBALL ->{
                            handleCollision(false)
                            if (!isGameActive) return
                        }
                        ItemType.COIN -> handleCollision(true)
                        else -> {}
                    }
                }
                itemGridState[rows - 1][col] = ItemType.NONE
            }
        }

        if (isGameActive) {
            for (row in rows - 2 downTo 0) {
                for (col in 0 until cols) {
                    val itemType = itemGridState[row][col]
                    if (itemType != ItemType.NONE) {
                        itemGridState[row][col] = ItemType.NONE
                        itemGridState[row + 1][col] = itemType
                    }
                }
            }
        }
    }

    private fun handleCollision(isCoin: Boolean) {
        if (isCoin) {
            listener.onCoinCollected()
        } else {
            lives--
            listener.onHit()

            if (lives <= 0) {
                lives = 0
                isGameActive = false
                listener.onGameOver()
            }
        }
    }
}