package com.example.androidDeviceDetails.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.activities.AppInfoActivity
import com.example.androidDeviceDetails.activities.BatteryActivity
import com.example.androidDeviceDetails.activities.LocationActivity
import com.example.androidDeviceDetails.activities.NetworkUsageActivity
import com.example.androidDeviceDetails.services.AppService
import com.example.androidDeviceDetails.utils.PrefManager
import com.example.androidDeviceDetails.utils.Utils

const val permissionCode = 200
val permissions: Array<String> =
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var toLocationActivityButton: Button
    private lateinit var appInfoButton: Button
    private lateinit var batteryInfoButton: Button
    private lateinit var appDataButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()
        if (!PrefManager.createInstance(this).getBoolean(PrefManager.INITIAL_LAUNCH, false)
        ) {
            Utils.addInitialData(this)
            PrefManager.createInstance(this)
                .putBoolean(PrefManager.INITIAL_LAUNCH, true)
        }
        init()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, AppService::class.java))
        else startService(Intent(this, AppService::class.java))
    }

    private fun init() {
        toLocationActivityButton = findViewById(R.id.toLocationActivity)
        toLocationActivityButton.setOnClickListener(this)
        appInfoButton = findViewById(R.id.appInfo)
        batteryInfoButton = findViewById(R.id.batteryInfo)
        appInfoButton.setOnClickListener(this)
        batteryInfoButton.setOnClickListener(this)
        appDataButton = findViewById(R.id.appData)
        appDataButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toLocationActivity -> startActivity(Intent(this, LocationActivity::class.java))
            R.id.batteryInfo -> startActivity(Intent(this, BatteryActivity::class.java))
            R.id.appInfo -> startActivity(Intent(this, AppInfoActivity::class.java))
            R.id.appData -> startActivity(Intent(this, NetworkUsageActivity::class.java))
        }
    }

    private fun requestPermissions() =
        ActivityCompat.requestPermissions(this, permissions, permissionCode)

}