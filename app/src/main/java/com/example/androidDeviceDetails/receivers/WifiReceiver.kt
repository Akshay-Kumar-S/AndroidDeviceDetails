package com.example.androidDeviceDetails.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.WifiRaw
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class WifiReceiver(private val context: Context) : BroadcastReceiver() {
    private var signalDB = RoomDB.getDatabase()!!

    override fun onReceive(context: Context, intent: Intent) {
        val wifiRaw: WifiRaw
        var level = 0
        var strength = 0
        var frequency = 0
        var linkSpeed = 0

        val wifiManager =
            context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        strength = wifiManager.connectionInfo.rssi
        frequency = wifiManager.connectionInfo.linkSpeed
        linkSpeed = wifiManager.connectionInfo.frequency
        level = getLevel(strength)

        WifiActivity.Wifi.strength = strength.toDouble()
        WifiActivity.Wifi.time = System.currentTimeMillis()

        wifiRaw = WifiRaw(
            System.currentTimeMillis(), strength, level, frequency, linkSpeed
        )
        Log.d("wifi", "onReceive: $strength,$level,$frequency,$linkSpeed")
        GlobalScope.launch {
            signalDB.wifiDao().insertAll(wifiRaw)
        }
    }

    private fun getLevel(strength: Int): Int {
        return when {
            strength > -50 -> 3
            strength > -60 -> 2
            strength > -70 -> 1
            else -> 0
        }
    }
}