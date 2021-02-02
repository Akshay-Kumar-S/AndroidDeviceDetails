package com.example.androidDeviceDetails.collectors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.collectors.BatteryCollector.BatteryReceiver
import com.example.androidDeviceDetails.database.BatteryRaw
import com.example.androidDeviceDetails.database.RoomDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *  Implements [BaseCollector].
 *  An event based collector which collects the battery usage data.
 *  Contains a [BroadcastReceiver] : [BatteryReceiver] which is registered on
 *  initialization of this class.
 *  This broadcast requires [android.Manifest.permission.BATTERY_STATS] permission.
 **/
class BatteryCollector : BaseCollector() {

    /**
     * A [BroadcastReceiver] which gets notified from [Intent.ACTION_BATTERY_CHANGED]
     **/
    object BatteryReceiver : BroadcastReceiver() {

        /**
         *  Receiver which gets notified when a battery event is occurred.
         *  Broadcast Action:
         *  [android.os.BatteryManager.EXTRA_LEVEL],
         *  [android.os.BatteryManager.EXTRA_PLUGGED],
         *  [android.os.BatteryManager.EXTRA_TEMPERATURE],
         *  [android.os.BatteryManager.EXTRA_HEALTH],
         *  [android.os.BatteryManager.EXTRA_LEVEL],
         *  [android.os.BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER],
         *  These events are made into a [BatteryRaw] and saved into the [RoomDB.batteryDao]
         *  This broadcast requires [android.Manifest.permission.BATTERY_STATS] permission.
         **/
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryManager =
                context?.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val db = RoomDB.getDatabase(context)
            val batteryRaw = BatteryRaw(
                System.currentTimeMillis(),
                intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0,
                intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0,
                intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)?.div(10) ?: 0,
                intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0),
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER),
                intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0,
            )
            GlobalScope.launch { db?.batteryDao()?.insert(batteryRaw) }
        }
    }

    /**
     * Registers the [BatteryReceiver] with [Intent.ACTION_BATTERY_CHANGED].
     **/
    override fun start() {
        DeviceDetailsApplication.instance.registerReceiver(
            BatteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    /**
     * Unregisters the [BatteryReceiver].
     **/
    override fun stop() = DeviceDetailsApplication.instance.unregisterReceiver(BatteryReceiver)
}