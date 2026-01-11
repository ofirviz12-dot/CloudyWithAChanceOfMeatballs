package com.example.cloudywithachanceofmeatballs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment() {

    var listener: OnScoreClickListener? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var scoreManager: ScoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView = view.findViewById(R.id.highScoreRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        scoreManager = ScoreManager(requireContext())

        loadHighScores()

        return view
    }

    private fun loadHighScores() {
        val scores = scoreManager.getTopScores()

        val adapter = HighScoresAdapter(scores, object : OnScoreClickListener {
            override fun onScoreClick(lat: Double, lng: Double) {
                listener?.onScoreClick(lat, lng)
            }
        })

        recyclerView.adapter = adapter
    }
}