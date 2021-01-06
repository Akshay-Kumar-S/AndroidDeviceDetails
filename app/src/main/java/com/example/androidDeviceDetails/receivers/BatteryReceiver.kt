package com.example.androidDeviceDetails.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseEventCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.batteryModels.BatteryEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BatteryReceiver : BaseEventCollector, BroadcastReceiver() {

    init {
        registerReceiver()
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val batteryManager: BatteryManager =
            context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val db = RoomDB.getDatabase(context)
        val batteryRaw = BatteryEntity(
            System.currentTimeMillis(),
            intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0),
            intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0),
            intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10,
            intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0),
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER),
            intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0),
        )
        GlobalScope.launch { db?.batteryDao()?.insertAll(batteryRaw) }
    }

    override fun registerReceiver() {
        DeviceDetailsApplication.instance.registerReceiver(
            this,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    override fun unregisterReceiver() {
        DeviceDetailsApplication.instance.unregisterReceiver(this)
    }

}