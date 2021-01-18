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
import com.example.androidDeviceDetails.utils.Utils
import com.fonfon.kgeohash.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LocationListener(private var locationManager: LocationManager, val context: Context) {
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!

    init {
        start()
    }

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
                    TimeUnit.SECONDS.toMillis(Utils.COLLECTION_INTERVAL),
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "gpsLocation not null $location")
                    locationGps = location
                    insert(locationGps)
                }
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasNetworkGpsp")
                locationManager.requestLocationUpdates(
                    NETWORK_PROVIDER,
                    TimeUnit.MINUTES.toMillis(Utils.COLLECTION_INTERVAL),
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "networkLocation not null $location")
                    locationNetwork = location
                    insert(locationNetwork)
                }
            }
            if (locationGps != null && locationNetwork != null) {
                Log.d("CodeAndroidLocation", "has both")
                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                    insert(locationNetwork)
                } else {
                    insert(locationGps)
                }
            }
        } else {
//            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    fun insert(location: Location?) {
        val geoHash = GeoHash(
            location!!.latitude,
            location.longitude,
            6
        ).toString()
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