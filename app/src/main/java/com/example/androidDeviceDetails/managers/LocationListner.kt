package com.example.androidDeviceDetails.managers

import android.location.LocationManager
import android.widget.Toast
import com.example.androidDeviceDetails.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.fonfon.kgeohash.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.util.Log

class LocationListner(var locationManager: LocationManager,var context: Context) {
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!

    @SuppressLint("MissingPermission")
    fun getLocation() {
        hasGps = locationManager.isProviderEnabled(GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(NETWORK_PROVIDER)
        Log.d("Location", "getLocationp: $hasGps $hasNetwork ")
        if (hasGps || hasNetwork) {
            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGpsp")
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "gpsLocation not nullp $location")
                    locationGps = location
                    insert(locationGps, "GPS")
                }
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasNetworkGpsp")
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "networkLocation not nullp $location")
                    locationNetwork = location
                    insert(locationNetwork, "Network")
                }
            }
            if (locationGps != null && locationNetwork != null) {
                Log.d("CodeAndroidLocation", "has bothp")
                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                    insert(locationNetwork, "mNetwork")
                } else {
                    insert(locationGps, "mGPS")
                }
            }
        } else {
//            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    fun insert(location: Location?, tag: String) {
        val geoHash = GeoHash(
            location!!.latitude,
            location.longitude,
            6
        ).toString()
        Log.d("CodeAndroidLocation", "Latitude : " + location.latitude)
        Log.d("CodeAndroidLocation", "Longitude : " + location.longitude)
        Log.d("CodeAndroidLocation", "GeoHash : $geoHash")
        Log.d("Date", "${Date(System.currentTimeMillis())}")
        val locationObj = LocationModel(
            0, location.latitude, location.longitude, geoHash,
            System.currentTimeMillis()
        )
        GlobalScope.launch {
            locationDatabase.locationDao().insertLocation(locationObj)
        }
        Toast.makeText(context, "Added to Database", Toast.LENGTH_LONG).show()
    }
}