package com.example.cloudywithachanceofmeatballs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    var listener: OnScoreClickListener? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView = view.findViewById(R.id.highScoreRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        database = AppDatabase.getDatabase(requireContext())

        loadHighScores()

        return view
    }

    private fun loadHighScores() {
        lifecycleScope.launch {
            val scores = database.scoreDao().getTopTenScores()

            val adapter = HighScoresAdapter(scores, object : OnScoreClickListener {
                override fun onScoreClick(lat: Double, lng: Double) {
                    listener?.onScoreClick(lat, lng)
                }
            })

            recyclerView.adapter = adapter
        }
    }
}