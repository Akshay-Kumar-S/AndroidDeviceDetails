package com.example.androidDeviceDetails

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.androidDeviceDetails.utils.PrefManager
import com.example.androidDeviceDetails.utils.Utils

const val permissionCode = 200
val permissions: Array<String> =
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var toLocationActivityButton: Button
    private lateinit var appInfoButton: Button
    private lateinit var batteryInfoButton: Button
    private lateinit var toggleServiceButton: Button
    private lateinit var text: TextView
    private lateinit var signalButton: Button
    private lateinit var strength:Button

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
        strength=findViewById(R.id.button)
        strength.setOnClickListener(){
            val send = Intent(this@MainActivity, NavActivity::class.java)
            startActivity(send)
        }
        init()

    }

    private fun init() {
        toLocationActivityButton = findViewById(R.id.toLocationActivity)
        toLocationActivityButton.setOnClickListener(this)
        appInfoButton = findViewById(R.id.appInfo)
        batteryInfoButton = findViewById(R.id.batteryInfo)
        appInfoButton.setOnClickListener(this)
        batteryInfoButton.setOnClickListener(this)
        toggleServiceButton = findViewById(R.id.toggleSwitch)
        toggleServiceButton.setOnClickListener(this)
        signalButton = findViewById(R.id.signal)
        signalButton.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.toLocationActivity -> {
                val intent = Intent(this, LocationActivity::class.java).apply {}
                startActivity(intent)
            }
            R.id.batteryInfo -> {
                val intent = Intent(this, BatteryActivity::class.java).apply {}
                startActivity(intent)
            }
            R.id.appInfo ->{
                val intent = Intent(this, AppInfoActivity::class.java).apply {}
                startActivity(intent)
            }
            R.id.toggleSwitch -> toggleService()
            R.id.signal -> startActivity(Intent(this, SignalStrengthActivity::class.java))
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            permissionCode
        )
    }

    private fun toggleService() {
        val mainController = MainController()
        mainController.toggleService(this)
    }

}