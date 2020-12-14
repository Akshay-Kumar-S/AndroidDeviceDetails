package com.example.androidDeviceDetails

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.managers.AppBatteryUsageManager


class BatteryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)
        val batteryListView = findViewById<ListView>(R.id.batteryListView)
        AppBatteryUsageManager().cookBatteryData(this, batteryListView)

//        batteryListView.setOnItemClickListener { parent:AdapterView<*>, view:View, position:Int,id:Long ->
//            Log.d("TAG", "${view.id} $position $id ")
//        }
    }
}

