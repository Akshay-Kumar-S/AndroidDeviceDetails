package com.example.androidDeviceDetails.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.WifiRaw

class WifiReceiver : BaseCollector() {
    private var signalDB = RoomDB.getDatabase()!!

    object broadcastReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val wifiRaw: WifiRaw
            var level = 0
            var strength = 0

            val wifiManager =
                context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
            strength = wifiManager.connectionInfo.rssi
            //could add link speed, frequency
            //Log.d("details", "onReceive: ${wifiManager.connectionInfo.ipAddress}   ${wifiManager.connectionInfo.linkSpeed} ${wifiManager.connectionInfo.frequency} ${wifiManager.connectionInfo.macAddress}")
            level = WifiReceiver().getLevel(strength)
            wifiRaw = WifiRaw(
                System.currentTimeMillis(), strength, level
            )

            Log.d("wifi", "onReceive: $strength,$level")
            /*GlobalScope.launch {
                signalDB.wifiDao().insertAll(wifiRaw)
            }*/
        }
    }

    init {
        start()
    }

    private fun getLevel(strength: Int): Int {
        return when {
            strength > -50 -> 3
            strength > -60 -> 2
            strength > -70 -> 1
            else -> 0
        }
    }

    override fun start() {
        val intentWifi = IntentFilter()
        intentWifi.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        DeviceDetailsApplication.instance.registerReceiver(broadcastReceiver, intentWifi)
    }

    override fun collect() {
    }

    override fun stop() {
        DeviceDetailsApplication.instance.unregisterReceiver(broadcastReceiver)
    }
}