package com.example.androidDeviceDetails.collectors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.collectors.WifiCollector.WifiReceiver
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalRaw
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
            val signalRaw: SignalRaw
            val strength: Int
            val linkSpeed: Int
            val level: Int
            val operatorName: String
            var wifiPercentage=0F

            val wifiManager: WifiManager =
                context?.applicationContext?.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
            strength = wifiManager.connectionInfo.rssi
            linkSpeed = wifiManager.connectionInfo.linkSpeed
            operatorName = wifiManager.connectionInfo.ssid
            level = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                wifiManager.calculateSignalLevel(strength)
            } else {
                WifiManager.calculateSignalLevel(strength, 5)
            }
            wifiPercentage=WifiManager.calculateSignalLevel(strength, 45)/45.toFloat()*100
            val db = RoomDB.getDatabase(context)
            signalRaw = SignalRaw(
                System.currentTimeMillis(),
                Signal.WIFI.ordinal,
                strength,
                linkSpeed.toString(),
                level,
                operatorName,
                null,
                null,
                wifiPercentage

            )
            GlobalScope.launch {
                db?.signalDao()?.insertAll(signalRaw)
            }
        }

        /**
         * Retrieve level for wifi signal.
         * @param strength whose level is to be found
         * @return a single integer from 0 to 4 representing general signal quality. 0 represents
         * very poor while 4 represents excellent signal quality.
         **/
        /*    private fun getWifiLevel(strength: Int): Int {
                return when {
                    strength > -30 -> WifiLevel.GREAT.ordinal
                    strength > -50 -> WifiLevel.GOOD.ordinal
                    strength > -60 -> WifiLevel.MODERATE.ordinal
                    strength > -70 -> WifiLevel.POOR.ordinal
                    else -> WifiLevel.NONE.ordinal
                }
            }*/
    }

    init {
        start()
    }

    /**
     * Registers the [WifiCollector] with [WifiManager.RSSI_CHANGED_ACTION]
     * and [WifiManager.SCAN_RESULTS_AVAILABLE_ACTION].
     **/
    override fun start() {
        val filter = IntentFilter()
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        DeviceDetailsApplication.instance.registerReceiver(
            WifiReceiver,
            filter
        )
    }

    override fun collect() {
    }

    /**
     * Unregisters the [WifiCollector].
     **/
    override fun stop() {
        DeviceDetailsApplication.instance.unregisterReceiver(WifiReceiver)
    }
}
