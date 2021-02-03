package com.example.androidDeviceDetails.collectors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.collectors.WifiCollector.WifiReceiver
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.database.SignalRaw
import com.example.androidDeviceDetails.models.signal.Signal
import com.example.androidDeviceDetails.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *  Implements [BaseCollector].
 *  An event based collector which collects the WIFI signal data.
 *  Contains a [BroadcastReceiver] : [WifiReceiver] which is registered on
 *  initialization of this class.
 *  This broadcast requires [android.Manifest.permission.ACCESS_WIFI_STATE] permission.
 **/
class WifiCollector(val context: Context) : BaseCollector() {

    /**
     * Registers the [WifiCollector] with [WifiManager.RSSI_CHANGED_ACTION]
     * and [WifiManager.SCAN_RESULTS_AVAILABLE_ACTION].
     **/
    override fun start() {
        val wifiIntentFilter = IntentFilter()
        wifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        context.registerReceiver(WifiReceiver, wifiIntentFilter)
    }

    /**
     * Unregisters the [WifiCollector].
     **/
    override fun stop() {
        context.unregisterReceiver(WifiReceiver)
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
                    wifiPercentage =
                        (Signal.WIFI_MIN - strength) / Signal.WIFI_RANGE.toFloat() * 100
                }
                else -> {
                    level = WifiManager.calculateSignalLevel(strength, 5)
                    /*                  wifiPercentage = WifiManager.calculateSignalLevel(strength, Signal.WIFI_LEVEL) /
                                              Signal.WIFI_LEVEL.toFloat() * 100*/
                    wifiPercentage =
                        (strength - Signal.WIFI_MIN) / Signal.WIFI_RANGE.toFloat() * 100
                }
            }

            val signalRaw = SignalRaw(
                timeStamp = System.currentTimeMillis(),
                signal = Signal.WIFI,
                strength = strength,
                cellInfoType = null,
                linkSpeed = wifiManager.connectionInfo.linkSpeed,
                level = Utils.getSignalLevel(level),
                operatorName = wifiManager.connectionInfo.ssid,
                isRoaming = false,
                band = null,
                strengthPercentage = wifiPercentage
            )
            GlobalScope.launch { db?.signalDao()?.insert(signalRaw) }
        }
    }
}