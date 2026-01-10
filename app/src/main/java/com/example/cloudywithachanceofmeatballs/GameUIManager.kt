package com.example.cloudywithachanceofmeatballs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.cloudywithachanceofmeatballs.databinding.ActivityMainBinding

class GameUIManager(private val binding: ActivityMainBinding) {

    private val meatballsGrid = Array(6) { arrayOfNulls<ImageView>(6) }
    private val playerCols = arrayOfNulls<ImageView>(5)

    init {
        initGridViews()
        initPlayerViews()
    }
    fun showToast(context: Context, message: String) {
        try {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.my_toast, null)

            val text: TextView = layout.findViewById(R.id.toast_text)
            text.text = message

            val toast = Toast(context)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()
        } catch (e: Exception) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initGridViews() {
        //row 1
        meatballsGrid[0][0] = binding.cell00;
        meatballsGrid[0][1] = binding.cell01;
        meatballsGrid[0][2] = binding.cell02;
        meatballsGrid[0][3] = binding.cell03;
        meatballsGrid[0][4] = binding.cell04

        //row2
        meatballsGrid[1][0] = binding.cell10;
        meatballsGrid[1][1] = binding.cell11;
        meatballsGrid[1][2] = binding.cell12;
        meatballsGrid[1][3] = binding.cell13;
        meatballsGrid[1][4] = binding.cell14

        //row3
        meatballsGrid[2][0] = binding.cell20;
        meatballsGrid[2][1] = binding.cell21;
        meatballsGrid[2][2] = binding.cell22;
        meatballsGrid[2][3] = binding.cell23;
        meatballsGrid[2][4] = binding.cell24

        //row4
        meatballsGrid[3][0] = binding.cell30;
        meatballsGrid[3][1] = binding.cell31;
        meatballsGrid[3][2] = binding.cell32;
        meatballsGrid[3][3] = binding.cell33;
        meatballsGrid[3][4] = binding.cell34

        //row5
        meatballsGrid[4][0] = binding.cell40;
        meatballsGrid[4][1] = binding.cell41;
        meatballsGrid[4][2] = binding.cell42;
        meatballsGrid[4][3] = binding.cell43;
        meatballsGrid[4][4] = binding.cell44;

        //row6
        meatballsGrid[5][0] = binding.cell50;
        meatballsGrid[5][1] = binding.cell51;
        meatballsGrid[5][2] = binding.cell52;
        meatballsGrid[5][3] = binding.cell53;
        meatballsGrid[5][4] = binding.cell54;
    }

    private fun initPlayerViews() {
        playerCols[0] = binding.leftCol0
        playerCols[1] = binding.leftCol1
        playerCols[2] = binding.flint
        playerCols[3] = binding.rightCol0
        playerCols[4] = binding.rightCol1
    }

    fun updateGrid(logicalGrid: Array<Array<ItemType>>) {
        for (row in 0..5) {
            for (col in 0..4) {
                val itemType = logicalGrid[row][col]
                val imageView = meatballsGrid[row][col]

                when (itemType) {
                    ItemType.MEATBALL -> imageView?.setImageResource(R.drawable.meatball)
                    ItemType.COIN -> imageView?.setImageResource(R.drawable.dollar)
                    ItemType.NONE -> imageView?.setImageResource(android.R.color.transparent)
                }
            }
        }
    }

    fun updatePlayerPosition(playerPos: Int) {
        for (i in 0..4) {
            if (i == playerPos) {
                playerCols[i]?.setImageResource(R.drawable.flint)
            } else {
                playerCols[i]?.setImageResource(android.R.color.transparent)
            }
        }
    }

    fun updateHearts(lives: Int) {
        binding.heart1.visibility = if (lives >= 1) View.VISIBLE else View.INVISIBLE
        binding.heart2.visibility = if (lives >= 2) View.VISIBLE else View.INVISIBLE
        binding.heart3.visibility = if (lives >= 3) View.VISIBLE else View.INVISIBLE
    }

    fun showHitEffect() {
        binding.flint.alpha = 0.3f
        binding.flint.postDelayed({ binding.flint.alpha = 1f }, 300)
    }

    fun updateArrowsVisibility(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        binding.leftArrow.visibility = visibility
        binding.rightArrow.visibility = visibility
    }
}