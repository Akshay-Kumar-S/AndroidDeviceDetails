package com.example.androidDeviceDetails.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.managers.AppEventCollector
import com.example.androidDeviceDetails.managers.NetworkUsageCollector
import com.example.androidDeviceDetails.managers.SignalChangeListener
import com.example.androidDeviceDetails.location.LocationListener
import com.example.androidDeviceDetails.managers.AppUsage
import com.example.androidDeviceDetails.receivers.AppStateReceiver
import com.example.androidDeviceDetails.receivers.BatteryReceiver
import com.example.androidDeviceDetails.receivers.WifiReceiver
import java.util.*

const val CHANNEL_ID = "androidDeviceDetails"

class CollectorService : Service() {

    private lateinit var timer: Timer
    private lateinit var mBatteryReceiver: BatteryReceiver
    private lateinit var mAppStateReceiver: AppStateReceiver
    private lateinit var mWifiReceiver: WifiReceiver
    private lateinit var mAppEventCollector: AppEventCollector
    private lateinit var mAppDataUsageCollector: NetworkUsageCollector
    private lateinit var mPhoneStateListener: SignalChangeListener

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        mBatteryReceiver = BatteryReceiver()
        mWifiReceiver = WifiReceiver()
        mAppStateReceiver = AppStateReceiver()
        mAppEventCollector = AppEventCollector(this)
        mPhoneStateListener = SignalChangeListener(this)
        mAppDataUsageCollector = NetworkUsageCollector(this)
        pushNotification()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mAppEventCollector.runTimer(1)
        mAppDataUsageCollector.runTimer(1)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBatteryReceiver.unregisterReceiver()
        mWifiReceiver.unregisterReceiver()
        mAppStateReceiver.unregisterReceiver()
        timer.cancel()
        stopSelf()
    }

    private fun pushNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Collecting Data",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Monitoring device usage")
                .setContentText("Background service running")
                .setSmallIcon(R.drawable.ic_baseline_settings_18)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .build()
            startForeground(1, notification)
        }
    }
}

