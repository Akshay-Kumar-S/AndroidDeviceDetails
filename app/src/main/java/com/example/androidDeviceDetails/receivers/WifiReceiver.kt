package com.example.androidDeviceDetails.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import com.example.androidDeviceDetails.utils.Signal
import com.example.androidDeviceDetails.utils.WifiLevel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WifiReceiver : BaseCollector() {

    object broadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val signalEntity: SignalEntity
            val strength: Int
            val linkSpeed: Int
            val level: Int

            val wifiManager: WifiManager =
                context?.applicationContext?.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
            strength = wifiManager.connectionInfo.rssi
            linkSpeed = wifiManager.connectionInfo.linkSpeed
            level = getWifiLevel(strength)

            val db = RoomDB.getDatabase(context)
            signalEntity = SignalEntity(
                System.currentTimeMillis(),
                Signal.WIFI.ordinal,
                strength,
                linkSpeed.toString(),
                level
            )
            GlobalScope.launch {
                db?.signalDao()?.insertAll(signalEntity)
            }
        }

        private fun getWifiLevel(strength: Int): Int {
            return when {
                strength > -30 -> WifiLevel.Great.ordinal
                strength > -50 -> WifiLevel.Good.ordinal
                strength > -60 -> WifiLevel.Moderate.ordinal
                strength > -70 -> WifiLevel.Poor.ordinal
                else -> WifiLevel.None.ordinal
            }
        }
    }

    init {
        start()
    }

    override fun start() {
        val filter = IntentFilter()
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        DeviceDetailsApplication.instance.registerReceiver(
            broadcastReceiver,
            filter
        )
    }

    override fun collect() {
    }

    override fun stop() {
        DeviceDetailsApplication.instance.unregisterReceiver(broadcastReceiver)
    }
}