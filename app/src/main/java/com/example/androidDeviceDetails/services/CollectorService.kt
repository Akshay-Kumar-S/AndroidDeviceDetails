package com.example.androidDeviceDetails.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import com.example.androidDeviceDetails.location.LocationListener
import com.example.androidDeviceDetails.managers.AppUsage
import com.example.androidDeviceDetails.receivers.AppStateReceiver
import com.example.androidDeviceDetails.receivers.BatteryReceiver
import com.example.androidDeviceDetails.receivers.WifiReceiver
import java.util.*

const val CHANNEL_ID = "androidDeviceDetails"

class CollectorService : Service() {

    private lateinit var timer: Timer
    private lateinit var mBatteryReceiver: BroadcastReceiver
    private lateinit var mAppStateReceiver: BroadcastReceiver
    private lateinit var mTelephonyManager: TelephonyManager

    //    private lateinit var mPhoneStateListener: SignalChangeListener
    private lateinit var mWifiReceiver: WifiReceiver
    private lateinit var locationManager: LocationManager
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        mBatteryReceiver = BatteryReceiver()
//        mPhoneStateListener = SignalChangeListener(this)
        mWifiReceiver = WifiReceiver(this)
        mTelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        mAppStateReceiver = AppStateReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        filter.addDataScheme("package")
        this.registerReceiver(mAppStateReceiver, filter)

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Collecting Data",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build()
            startForeground(1, notification)

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer = Timer()
        val timeInterval: Long = 1
        val appUsage = AppUsage(this)
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    appUsage.updateAppUsageDB(timeInterval)
                }
            },
            0, 1000 * 60 * timeInterval
        )
//        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        val intentWifi = IntentFilter()
        intentWifi.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

        this.registerReceiver(mBatteryReceiver, IntentFilter(LocationManager.KEY_LOCATION_CHANGED))
        this.registerReceiver(mWifiReceiver, intentWifi)
        LocationListener(locationManager, this).getLocation()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(mBatteryReceiver)
        this.unregisterReceiver(mWifiReceiver)
        this.unregisterReceiver(mAppStateReceiver)
        timer.cancel()
        stopSelf()
    }
}

