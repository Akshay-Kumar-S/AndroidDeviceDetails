package com.example.androidDeviceDetails

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.utils.EventType
import com.example.androidDeviceDetails.utils.PrefManager
import com.example.androidDeviceDetails.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val permissionCode = 200
val permissions: Array<String> =
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var toLocationActivityButton: Button
    private lateinit var appInfoButton: Button
    private lateinit var toggleServiceButton: Button
    private lateinit var text: TextView
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
    }

    private fun init() {
        toLocationActivityButton = findViewById(R.id.toLocationActivity)
        toLocationActivityButton.setOnClickListener(this)
        appInfoButton = findViewById(R.id.appInfo)
        text = findViewById(R.id.textView)
        appInfoButton.setOnClickListener(this)
        toggleServiceButton = findViewById(R.id.toggleSwitch)
        toggleServiceButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.toLocationActivity -> {
                val intent = Intent(this, LocationActivity::class.java).apply {}
                startActivity(intent)
            }
            R.id.appInfo ->{
                val intent = Intent(this, AppInfoActivity::class.java).apply {}
                startActivity(intent)
            }
            R.id.toggleSwitch -> toggleService()
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