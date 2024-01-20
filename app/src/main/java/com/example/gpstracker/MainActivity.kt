package com.example.gpstracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions

class MainActivity : AppCompatActivity(), LocationListener, OnMapReadyCallback {
    private lateinit var locationManager: LocationManager
    private lateinit var myLatitude: TextView
    private lateinit var myLongitude: TextView
    private val locationPermissionCode = 2
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private lateinit var speedometer: SpeedometerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myLatitude = findViewById(R.id.myLatitude)
        myLongitude = findViewById(R.id.myLongitude)
        speedometer = findViewById(R.id.speedometer)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            // Request location updates when permission is granted
            requestLocationUpdates()
            // Get the last known location and display it
            displayLastKnownLocation()
        }

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        // Initialize the MapView and set the callback
        val options = GoogleMapOptions()
        options.mapType(GoogleMap.MAP_TYPE_NORMAL) // You can choose a different map type
        mapView.getMapAsync(this)
    }

    private fun requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1f, this)
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    private fun displayLastKnownLocation() {
        try {
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                myLatitude.text = "${lastKnownLocation.latitude}"
                myLongitude.text = "${lastKnownLocation.longitude}"
            } else {
                // Handle the case where there is no last known location available
                requestLocationUpdates()
            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
        myLatitude.text = "${location.latitude}"
        myLongitude.text = "${location.longitude}"
        speedometer.setSpeedMetersPerSecond(location.speed)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
                displayLastKnownLocation()
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), locationPermissionCode)
        }
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_in_night));
        val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (lastKnownLocation != null) {
            val currentLatLng = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)

            // Define a zoom level (you can adjust this as needed)
            val zoomLevel = 15f

            // Create a CameraPosition and move the camera to it
            val cameraPosition = CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(zoomLevel)
                .build()

            googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}
