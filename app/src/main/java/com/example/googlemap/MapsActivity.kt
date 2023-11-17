package com.example.googlemap

import com.example.googlemap.databinding.ActivityMapsBinding

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.appcompat.widget.SearchView
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchView = findViewById(R.id.idSearchView)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are getting the
                // location name from search view.
                val location = searchView.query.toString()

                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null

                // checking if the entered location is null or not.
                if (location != null || location != "") {
                    // on below line we are creating and initializing a geo coder.
                    val geocoder = Geocoder(this@MapsActivity)
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    val address: Address = addressList?.get(0)!!

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    val latLng = LatLng(address.latitude, address.longitude)

                    // on below line we are adding marker to that position.
                    mMap.addMarker(MarkerOptions().position(latLng).title(location))

                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                }
               Toast.makeText(this@MapsActivity,"search invailed",Toast.LENGTH_LONG).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        mapFragment.getMapAsync(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME_BETWEEN_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES,
            this
        )

    }



    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap=googleMap
        // Enable the location button and set the position
       mMap.isMyLocationEnabled=true
        mMap.setPadding(10,1000,15,50)

        // Example: Add a marker with a custom title and snippet
//        val markerLatLng = LatLng(37.7749, -122.4194) // Example LatLng, replace with your desired coordinates
//        val markerOptions = MarkerOptions()
//            .position(markerLatLng)
//            .title("Authenticode")
//            .snippet("")
//
//        mMap.addMarker(markerOptions)
//
//        // You can also move and animate the camera to focus on the marker
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng))
//        mMap.animateCamera(
//            CameraUpdateFactory.newCameraPosition(
//                CameraPosition.fromLatLngZoom(markerLatLng, 15f)
//            )
//        )

        // Add a click listener to the map
        mMap.setOnMapClickListener { latLng ->
            // Clear existing markers
            mMap.clear()

            // Add a new marker at the clicked location
            mMap.addMarker(MarkerOptions()
                .position(latLng)
                .title("location")
                .snippet("Latitude: ${latLng.latitude}, Longitude: ${latLng.longitude}"))

            // Move and animate the camera to focus on the new marker
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 15f)))
        }

    }

    override fun onLocationChanged(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        mMap.clear() // Clear existing markers

        // Display latitude and longitude on the marker
        val markerOptions = MarkerOptions()
            .position(currentLatLng)
            .title("Current Location")
            .snippet("Latitude: ${location.latitude}, Longitude: ${location.longitude}")

        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(currentLatLng, 15f)))
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 5000
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 1f
    }
}
