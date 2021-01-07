package com.example.androidDeviceDetails.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ApplicationController
import com.example.androidDeviceDetails.utils.Utils

const val CHANNEL_ID = "androidDeviceDetails"

class AppService : Service() {

    private lateinit var appController: ApplicationController

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        appController = ApplicationController()
        pushNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        appController.runTimer(Utils.COLLECTION_INTERVAL )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        appController.mBatteryReceiver.stop()
        appController.mWifiReceiver.stop()
        appController.mAppStateReceiver.stop()
        appController.timer.cancel()
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

