package com.example.androidDeviceDetails.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ApplicationController
import com.example.androidDeviceDetails.utils.Utils

const val CHANNEL_ID = "androidDeviceDetails"
private const val TAG = "AppService"

class AppService : Service() {

    private lateinit var appController : ApplicationController

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind")
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        appController = ApplicationController()
        pushNotification()
        appController.runTimer(Utils.COLLECTION_INTERVAL )
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: AppService onDestroy called")
        for (receivers in appController.instanceMap.values)
            receivers.stop()
        appController.timer.cancel()
        stopSelf()
        super.onDestroy()
    }

    private fun pushNotification() {
        Log.d(TAG, "pushNotification")
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