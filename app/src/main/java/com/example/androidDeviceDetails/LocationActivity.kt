
package com.example.androidDeviceDetails

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.models.CountModel
import com.example.androidDeviceDetails.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.fonfon.kgeohash.GeoHash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private const val PERMISSION_REQUEST = 10

class LocationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var locationManager: LocationManager
    private lateinit var locationCounter: LocationCounter
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private lateinit var geoHashView: TextView
    private lateinit var countLocation: Button
    private lateinit var btnGetLocation: Button

    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var locationDatabase: RoomDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        geoHashView = findViewById(R.id.geoHashView)
        countLocation = findViewById(R.id.countLocation)
        btnGetLocation = findViewById(R.id.btnGetLocation)
        btnGetLocation.setOnClickListener(this)
        countLocation.setOnClickListener(this)
        locationCounter = LocationCounter()
        locationDatabase = RoomDB.getDatabase()!!
        disableView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                enableView()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            enableView()
        }
    }

    private fun disableView() {
        val btnGetLocation: Button = findViewById(R.id.btnGetLocation)
        btnGetLocation.isEnabled = false
        btnGetLocation.alpha = 0.5F
    }

    private fun enableView() {
        val btnGetLocation: Button = findViewById(R.id.btnGetLocation)
        btnGetLocation.isEnabled = true
        btnGetLocation.alpha = 1F
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
    }

    fun insert(location: Location?, tag: String) {
        val locationView: TextView = findViewById(R.id.textView)
        locationView.append("$tag \nLatitude: " + location!!.latitude + "\nLongitude: " + location.longitude + "\nTimestamp: " + Calendar.getInstance().time.toString() + "\n\n")
        val geoHash = GeoHash(
            location.latitude,
            location.longitude,
            6
        ).toString()
        Log.d("Time", Calendar.getInstance().time.toString())
        Log.d("CodeAndroidLocation", "Latitude : " + location.latitude)
        Log.d("CodeAndroidLocation", "Longitude : " + location.longitude)
        Log.d("CodeAndroidLocation", "GeoHash : $geoHash")
        val locationObj = LocationModel(
            0, location.latitude, location.longitude, geoHash,
            Calendar.getInstance().time.toString()
        )
        GlobalScope.launch {
            locationDatabase.locationDao().insertLocation(locationObj)
        }
        Toast.makeText(applicationContext, "Added to Database", Toast.LENGTH_LONG).show()
    }

    private suspend fun getData(): List<LocationModel> = withContext(Dispatchers.IO) {
        return@withContext locationDatabase.locationDao().readAll()
    }

    private suspend fun countLocation(): List<CountModel> = withContext(Dispatchers.IO) {
        return@withContext locationDatabase.locationDao().countHash()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {
            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "gpsLocation not null")
                    locationGps = location
                    insert(locationGps, "GPS")
                }
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasNetworkGps")
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "networkLocation not null")
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
            GlobalScope.launch {
                val result = getData()
                if (result.isNotEmpty()) {
                    for (i in result.indices) {
                        Log.d("Latitude", result[i].latitude.toString())
                        Log.d("Longitude", result[i].longitude.toString())
                        Log.d("Time", result[i].time.toString())
                    }
                }
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain =
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                            permissions[i]
                        )
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Go to settings and enable the permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (allSuccess)
                enableView()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnGetLocation -> getLocation()
            R.id.countLocation -> {
                GlobalScope.launch {
                    val res = countLocation()
                    geoHashView.post {
                        geoHashView.text = ""
                        for (hash in res) {
                            geoHashView.append("GeoHash:" + hash.geoHash + " = " + hash.count + "\n")
                        }
                    }
                }
            }
        }
    }

}