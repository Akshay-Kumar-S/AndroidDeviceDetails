package com.example.androidDeviceDetails.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import com.example.androidDeviceDetails.utils.Signal
import com.example.androidDeviceDetails.utils.WifiLevel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class WifiReceiver : BroadcastReceiver() {
    private var db = RoomDB.getDatabase()!!

    override fun onReceive(context: Context, intent: Intent) {
        val signalEntity: SignalEntity
        val strength: Int
        val linkSpeed: Int
        val level: Int

        val wifiManager =
            context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        strength = wifiManager.connectionInfo.rssi
        linkSpeed = wifiManager.connectionInfo.linkSpeed
        level = getWifiLevel(strength)
        Log.d("wifi", "data: $strength, $level,$linkSpeed")
        signalEntity = SignalEntity(
            System.currentTimeMillis(), Signal.WIFI.ordinal, strength, linkSpeed.toString(), level
        )
        GlobalScope.launch {
            db.signalDao().insertAll(signalEntity)
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
