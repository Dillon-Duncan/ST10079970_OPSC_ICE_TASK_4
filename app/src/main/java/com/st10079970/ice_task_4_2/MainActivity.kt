package com.st10079970.ice_task_4_2

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double = 0.0
    private var currentLon: Double = 0.0
    private lateinit var searchBar: EditText
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        searchBar = findViewById(R.id.txtSearchBar)
        searchButton = findViewById(R.id.btnSearch)
        recyclerView = findViewById(R.id.rcvLocationView)

        setupUI()
        checkLocationPermission()

        searchButton.setOnClickListener {
            val query = searchBar.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchNearbyPlaces(query)
            } else {
                Toast.makeText(this, "Please enter a location to search", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class PlacesAdapter(private var placesList: List<Place>) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
            return PlaceViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
            val place = placesList[position]
            holder.bind(place)
        }

        override fun getItemCount() = placesList.size

        fun updatePlaces(newPlaces: List<Place>) {
            placesList = newPlaces
            notifyDataSetChanged()
        }

        class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(place: Place) {
                itemView.findViewById<TextView>(R.id.placeName).text = place.name
                itemView.findViewById<TextView>(R.id.placeAddress).text = place.address
                itemView.findViewById<TextView>(R.id.placeRating).text = place.rating.toString()
                itemView.findViewById<TextView>(R.id.placeDistance).text = String.format("%.2f km", place.distance)
            }
        }
    }


    private fun setupUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PlacesAdapter(emptyList())
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        currentLat = it.latitude
                        currentLon = it.longitude
                        fetchNearbyPlaces()
                    }
                }
        }
    }

    private fun fetchNearbyPlaces(query: String = "") {
        val apiKey = "AIzaSyA9-IOSS-TbEVgOnlMQ5BMiZkpd6IhDqzk"
        val location = "$currentLat,$currentLon"
        val radius = "1500"
        val type = if (query.isEmpty()) "restaurant" else query
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$location&radius=$radius&type=$type&key=$apiKey"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val places = parsePlaces(response)
                updateRecyclerView(places)
            },
            { error ->
                Toast.makeText(this, "Failed to load places", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun parsePlaces(response: JSONObject): List<Place> {
        val places = mutableListOf<Place>()
        val results: JSONArray = response.optJSONArray("results") ?: return emptyList()

        for (i in 0 until results.length()) {
            val placeJson = results.getJSONObject(i)
            val name = placeJson.optString("name", "N/A")
            val address = placeJson.optString("vicinity", "N/A")
            val rating = placeJson.optDouble("rating", 0.0).toFloat()

            val location = placeJson.getJSONObject("geometry").getJSONObject("location")
            val lat = location.optDouble("lat", 0.0)
            val lon = location.optDouble("lng", 0.0)

            val distance = calculateDistance(currentLat, currentLon, lat, lon)
            places.add(Place(name, address, rating, distance))
        }
        return places
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    private fun updateRecyclerView(places: List<Place>) {
        recyclerView.adapter = PlacesAdapter(places)
    }

    data class Place(val name: String, val address: String, val rating: Float, val distance: Double)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
