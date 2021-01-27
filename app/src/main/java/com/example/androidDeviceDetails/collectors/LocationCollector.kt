package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.util.Log
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.database.LocationModel
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LocationCollector(private val context: Context) : BaseCollector() {
    private lateinit var locationGps: Location
    private lateinit var locationNetwork: Location
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!
    private var locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

    override fun start() {
        val hasGps = locationManager.isProviderEnabled(GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(NETWORK_PROVIDER)
        Log.d("Location", "getLocation: $hasGps $hasNetwork ")
        if (context.checkCallingOrSelfPermission(
                "android.Manifest.permission.ACCESS_FINE_LOCATION"
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGpsp")
                locationManager.requestLocationUpdates(
                    GPS_PROVIDER, TimeUnit.MINUTES.toMillis(Utils.COLLECTION_INTERVAL), 0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "gpsLocation not null $location")
                    locationGps = location
                }
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasNetworkGpsp")
                locationManager.requestLocationUpdates(
                    NETWORK_PROVIDER, TimeUnit.MINUTES.toMillis(Utils.COLLECTION_INTERVAL), 0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "networkLocation not null $location")
                    locationNetwork = location
                }
            }
        }
    }

    override fun collect() {
        Log.d("Collect Location", "has")
        if (this::locationGps.isInitialized && this::locationNetwork.isInitialized) {
            Log.d("CodeAndroidLocation", "has both")
            if (locationGps.accuracy > locationNetwork.accuracy) {
                insert(locationNetwork)
            } else {
                insert(locationGps)
            }
        } else if (this::locationGps.isInitialized) {
            insert(locationGps)
        } else if (this::locationNetwork.isInitialized) {
            insert(locationNetwork)
        }
    }

    fun insert(location: Location) {
        val locationObj = LocationModel(
            0, location.latitude, location.longitude, System.currentTimeMillis()
        )
        GlobalScope.launch {
            locationDatabase.locationDao().insertLocation(locationObj)
        }
    }
}