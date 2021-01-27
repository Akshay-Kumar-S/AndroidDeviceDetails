package com.example.androidDeviceDetails.collectors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.collectors.WifiCollector.WifiReceiver
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.database.SignalRaw
import com.example.androidDeviceDetails.utils.Signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *  Implements [BaseCollector].
 *  An event based collector which collects the WIFI signal data.
 *  Contains a [BroadcastReceiver] : [WifiReceiver] which is registered on
 *  initialization of this class.
 *  This broadcast requires [android.Manifest.permission.ACCESS_WIFI_STATE] permission.
 **/
class WifiCollector : BaseCollector() {

    companion object {
        const val WIFI_MIN: Int = -120
        const val WIFI_LEVEL = 45
        const val WIFI_RANGE = 97
    }

    /**
     * A [BroadcastReceiver] which gets notified from [WifiManager.RSSI_CHANGED_ACTION] and
     * [WifiManager.SCAN_RESULTS_AVAILABLE_ACTION].
     **/
    object WifiReceiver : BroadcastReceiver() {

        /**
         *  Receiver which gets notified when a change in wifi signal strength occurs and also
         *  when a scan is complete.
         *   Method collects current timestamp, signal, strength, linkSpeed and level.
         *  These values are made into a [SignalRaw] and saved into the [RoomDB.signalDao].
         *  This broadcast requires [android.Manifest.permission.ACCESS_WIFI_STATE] permission.
         **/
        override fun onReceive(context: Context?, intent: Intent?) {
            val strength: Int
            val level: Int
            val wifiPercentage: Float

            val wifiManager: WifiManager =
                context?.applicationContext?.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
            val db = RoomDB.getDatabase(context)

            strength = wifiManager.connectionInfo.rssi
            @Suppress("DEPRECATION")
            when {
                Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> {
                    level = wifiManager.calculateSignalLevel(strength)
                    wifiPercentage = (WIFI_MIN - strength) / WIFI_RANGE.toFloat() * 100
                }
                else -> {
                    level = WifiManager.calculateSignalLevel(strength, 5)
                    wifiPercentage = WifiManager.calculateSignalLevel(
                        strength, WIFI_LEVEL
                    ) / WIFI_LEVEL.toFloat() * 100
                }
            }

            val signalRaw = SignalRaw(
                timeStamp = System.currentTimeMillis(),
                signal = Signal.WIFI.ordinal,
                strength = strength,
                cellInfoType = null,
                linkSpeed = wifiManager.connectionInfo.linkSpeed,
                level = level,
                operatorName = wifiManager.connectionInfo.ssid,
                isRoaming = null,
                band = null,
                strengthPercentage = wifiPercentage
            )
            GlobalScope.launch {
                db?.signalDao()?.insert(signalRaw)
            }
        }
    }

    /**
     * Registers the [WifiCollector] with [WifiManager.RSSI_CHANGED_ACTION]
     * and [WifiManager.SCAN_RESULTS_AVAILABLE_ACTION].
     **/
    override fun start() {
        val wifiIntentFilter = IntentFilter()
        wifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        DeviceDetailsApplication.instance.registerReceiver(
            WifiReceiver, wifiIntentFilter
        )
    }

    /**
     * Unregisters the [WifiCollector].
     **/
    override fun stop() {
        DeviceDetailsApplication.instance.unregisterReceiver(WifiReceiver)
    }
}