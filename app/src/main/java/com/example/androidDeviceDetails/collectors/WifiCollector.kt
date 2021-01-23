package com.example.androidDeviceDetails.collectors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
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
            when {
                Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> {
                    level = wifiManager.calculateSignalLevel(strength)
                    wifiPercentage=(-127-strength)/103.toFloat()*100
                }
                else -> {
                    level = WifiManager.calculateSignalLevel(strength, 5)
                    wifiPercentage=WifiManager.calculateSignalLevel(strength, 45)/45.toFloat()*100
                }
            }

            //TODO wifi percentage,move declaration to up


            val wifiInfo: WifiInfo
            val ssid:String
            wifiInfo = wifiManager.connectionInfo
            if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                ssid = wifiInfo.ssid
            }

            val db = RoomDB.getDatabase(context)
            signalRaw = SignalRaw(
                System.currentTimeMillis(),
                Signal.WIFI.ordinal,
                strength,
                null,
                linkSpeed,
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

    /**
     * Unregisters the [WifiCollector].
     **/
    override fun stop() {
        DeviceDetailsApplication.instance.unregisterReceiver(WifiReceiver)
    }
}