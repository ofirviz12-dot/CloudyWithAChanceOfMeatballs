package com.example.cloudywithachanceofmeatballs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.mapFragmentContainer, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        loadAllMarkers()
    }
    private fun loadAllMarkers() {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val scores = database.scoreDao().getTopTenScores()

            if (scores.isNotEmpty()) {
                scores.forEach { score ->
                    val position = LatLng(score.lat, score.lng)
                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title("${score.playerName}: ${score.distance}m")
                    )
                }
                val firstScoreLoc = LatLng(scores[0].lat, scores[0].lng)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(firstScoreLoc, 10f))
            } else {
                val defaultLoc = LatLng(32.0853, 34.7818)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 10f))
            }
        }
    }

    fun zoomToLocation(lat: Double, lng: Double) {
        googleMap?.let {
            val position = LatLng(lat, lng)
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        }
    }
}
