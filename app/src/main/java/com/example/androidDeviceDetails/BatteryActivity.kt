package com.example.androidDeviceDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.managers.AppBatteryUsageManager
import com.example.androidDeviceDetails.managers.AppEntry


class BatteryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)
        val batteryListView = findViewById<ListView>(R.id.batteryListView)
        val totalTextView = findViewById<TextView>(R.id.total)

        val items = arrayListOf<AppEntry>()
        items.add(AppEntry("com.whatsapp", 6))
        items.add(AppEntry("com.example.Broadband", 4))
        items.add(AppEntry("com.android.packageinstaller", 4))
        items.add(AppEntry("com.microsoft.teams", 4))
        items.add(AppEntry("com.google.android.apps.photos", 4))
        batteryListView.adapter = BatteryListAdapter(this, R.layout.battery_tile, items)

        AppBatteryUsageManager().cookBatteryData(this, batteryListView, totalTextView)
        batteryListView.setOnItemClickListener { parent, _, position, _ ->
            val adapter = parent.adapter as BatteryListAdapter
            val item = adapter.getItem(position)
            Log.d("TAG", "onCreate: $position")
            val infoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
            infoIntent.data = Uri.parse("package:${item?.packageId}")
            startActivity(infoIntent)
        }
    }
}

