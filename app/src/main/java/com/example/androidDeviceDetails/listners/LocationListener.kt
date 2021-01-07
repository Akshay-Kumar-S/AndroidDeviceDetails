package com.example.androidDeviceDetails.listners

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.util.Log
import android.widget.Toast
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.fonfon.kgeohash.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class LocationListener(private var locationManager: LocationManager, val context: Context) {
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!


    @SuppressLint("MissingPermission")
    fun start() {
        hasGps = locationManager.isProviderEnabled(GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(NETWORK_PROVIDER)
        Log.d("Location", "getLocation: $hasGps $hasNetwork ")
        if (hasGps || hasNetwork) {
            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGpsp")
                locationManager.requestLocationUpdates(
                    GPS_PROVIDER,
                    5000,
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "gpsLocation not null $location")
                    locationGps = location
                    insert(locationGps, "GPS")
                }
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasNetworkGpsp")
                locationManager.requestLocationUpdates(
                    NETWORK_PROVIDER,
                    5000,
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "networkLocation not null $location")
                    locationNetwork = location
                    insert(locationNetwork, "Network")
                }
            }
            if (locationGps != null && locationNetwork != null) {
                Log.d("CodeAndroidLocation", "has both")
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
        Log.d("CodeAndroidLocation $tag", "Latitude : " + location.latitude)
        Log.d("CodeAndroidLocation $tag", "Longitude : " + location.longitude)
        Log.d("CodeAndroidLocation $tag", "GeoHash : $geoHash")
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