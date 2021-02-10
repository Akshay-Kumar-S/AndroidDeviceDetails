package com.example.androidDeviceDetails.collectors

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
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
        val manager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        val hasGps = manager.isProviderEnabled(GPS_PROVIDER)
        val hasNetwork = manager.isProviderEnabled(NETWORK_PROVIDER)
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d("Location", "provider :${location.provider}")
                if (location.provider == "gps")
                    locationGps = location
                else
                    locationNetwork = location
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
        Log.d("LocationConstants", "gps:$hasGps network:$hasNetwork ")
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            && checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
        ) {
            if (hasGps)
                manager.requestLocationUpdates(GPS_PROVIDER, timeInterval(), 0F, listener)
            if (hasNetwork)
                manager.requestLocationUpdates(NETWORK_PROVIDER, timeInterval(), 0F, listener)
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

    private fun timeInterval() = TimeUnit.MINUTES.toMillis(Utils.COLLECTION_INTERVAL)
}