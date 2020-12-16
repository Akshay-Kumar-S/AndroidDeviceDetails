package com.example.androidDeviceDetails.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.androidDeviceDetails.LocationActivity
import com.example.androidDeviceDetails.managers.AppUsage
import com.example.androidDeviceDetails.managers.SignalChangeListener
import com.example.androidDeviceDetails.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.receivers.AppStateReceiver
import com.example.androidDeviceDetails.receivers.BatteryReceiver
import com.example.androidDeviceDetails.receivers.WifiReceiver
import com.fonfon.kgeohash.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

const val CHANNEL_ID = "androidDeviceDetails"

class CollectorService : Service() {

    private lateinit var timer: Timer
    private lateinit var mBatteryReceiver: BroadcastReceiver
    private lateinit var mAppStateReceiver: BroadcastReceiver
    private lateinit var mTelephonyManager: TelephonyManager
    private lateinit var mPhoneStateListener: SignalChangeListener
    private lateinit var mWifiReceiver: WifiReceiver
    private lateinit var locationManager: LocationManager
    private lateinit var locationDatabase: RoomDB
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        mBatteryReceiver = BatteryReceiver()
        mPhoneStateListener = SignalChangeListener(this)
        mWifiReceiver = WifiReceiver(this)
        mTelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationDatabase = RoomDB.getDatabase()!!

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
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        val intentWifi = IntentFilter()
        intentWifi.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

        this.registerReceiver(mBatteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        this.registerReceiver(mWifiReceiver, intentWifi)
        getLocation()
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

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.d("Location", "getLocation: $hasGps $hasNetwork ")
        if (hasGps || hasNetwork) {
            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "gpsLocation not null $location")
                    locationGps = location
                    insert(locationGps, "GPS")
                }
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasNetworkGps")
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F
                ) { location ->
                    Log.d("CodeAndroidLocation", "networkLocation not null $location")
                    locationNetwork = location
                    insert(locationNetwork, "Network")
                }
            }
            if (locationGps != null && locationNetwork != null) {
                Log.d("CodeAndroidLocation", "has both")
                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                    insert(locationNetwork, "mNetwork")
                } else {
                    insert(locationGps, "mGPS")
                }
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    fun insert(location: Location?, tag: String) {
        val geoHash = GeoHash(
            location!!.latitude,
            location.longitude,
            6
        ).toString()
        Log.d("Time", Calendar.getInstance().time.toString())
        Log.d("CodeAndroidLocation", "Latitude : " + location.latitude)
        Log.d("CodeAndroidLocation", "Longitude : " + location.longitude)
        Log.d("CodeAndroidLocation", "GeoHash : $geoHash")
        val locationObj = LocationModel(
            0, location.latitude, location.longitude, geoHash,
            Calendar.getInstance().time.toString()
        )
        GlobalScope.launch {
            locationDatabase.locationDao().insertLocation(locationObj)
        }
    }

}

