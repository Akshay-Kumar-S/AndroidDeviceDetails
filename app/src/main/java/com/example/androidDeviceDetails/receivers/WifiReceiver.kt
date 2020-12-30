package com.example.androidDeviceDetails.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.WifiRaw
import com.example.androidDeviceDetails.utils.SignalDbHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class WifiReceiver() : BroadcastReceiver() {
    private var db = RoomDB.getDatabase()!!

    override fun onReceive(context: Context, intent: Intent) {
        val wifiRaw: WifiRaw
        val strength: Int
        val frequency: Int
        val linkSpeed: Int

        val wifiManager =
            context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        strength = wifiManager.connectionInfo.rssi
        frequency = wifiManager.connectionInfo.linkSpeed
        linkSpeed = wifiManager.connectionInfo.frequency

        wifiRaw = WifiRaw(
            System.currentTimeMillis(), strength, frequency, linkSpeed
        )
        Log.d("wifi", "onReceive: $strength,$frequency,$linkSpeed")
        GlobalScope.launch {
            SignalDbHelper.writeToWifiDB(wifiRaw, db)
        }
    }
}