package com.example.androidDeviceDetails

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.managers.AppBatteryUsageManager
import com.example.androidDeviceDetails.managers.AppEntry


class BatteryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)
        val batteryListView = findViewById<ListView>(R.id.batteryListView)
        AppBatteryUsageManager().cookBatteryData(this,batteryListView)
    }
}

