package com.example.androidDeviceDetails.collectors

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.util.Log
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.database.LocationModel
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LocationCollector(private val context: Context) : BaseCollector() {

    private lateinit var locationGps: Location
    private lateinit var locationNetwork: Location

    override fun start() {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(NETWORK_PROVIDER)
        Log.d("LocationConstants", "gps:$hasGps network:$hasNetwork ")
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            && checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
        ) {
            if (hasGps) {
                locationManager.requestLocationUpdates(
                    GPS_PROVIDER, TimeUnit.MINUTES.toMillis(Utils.COLLECTION_INTERVAL), 0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "gpsLocation :$location")
                    locationGps = location
                }
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    NETWORK_PROVIDER, TimeUnit.MINUTES.toMillis(Utils.COLLECTION_INTERVAL), 0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "networkLocation :$location")
                    locationNetwork = location
                }
            }
        }
    }

    override fun collect() {
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
            RoomDB.getDatabase()!!.locationDao().insertLocation(locationObj)
        }
    }
}