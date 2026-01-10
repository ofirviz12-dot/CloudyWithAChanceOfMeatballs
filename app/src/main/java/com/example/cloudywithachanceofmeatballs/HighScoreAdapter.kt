package com.example.cloudywithachanceofmeatballs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HighScoresAdapter(
    private var scores: List<Score>,
    private val listener: OnScoreClickListener
) : RecyclerView.Adapter<HighScoresAdapter.ScoreViewHolder>() {

    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerName: TextView = view.findViewById(R.id.playerName)
        val playerScore: TextView = view.findViewById(R.id.playerScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val currentScore = scores[position]

        holder.playerName.text = "${position + 1}. ${currentScore.playerName}"
        holder.playerScore.text = "${currentScore.distance}m"

        holder.itemView.setOnClickListener {
            listener.onScoreClick(currentScore.lat, currentScore.lng)
        }
    }

    override fun getItemCount() = scores.size

    fun updateScores(newScores: List<Score>) {
        scores = newScores
        notifyDataSetChanged()
    }
}