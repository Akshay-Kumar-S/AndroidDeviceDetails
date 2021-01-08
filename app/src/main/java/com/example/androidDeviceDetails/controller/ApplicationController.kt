package com.example.androidDeviceDetails.controller

import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.listners.LocationListener
import com.example.androidDeviceDetails.managers.AppEventCollector
import com.example.androidDeviceDetails.managers.NetworkUsageCollector
import com.example.androidDeviceDetails.managers.SignalChangeListener
import com.example.androidDeviceDetails.receivers.AppInfoReceiver
import com.example.androidDeviceDetails.receivers.BatteryReceiver
import com.example.androidDeviceDetails.receivers.WifiReceiver
import java.util.*

class ApplicationController {
    var mBatteryReceiver: BatteryReceiver
    var mAppStateReceiver: AppInfoReceiver
    var mWifiReceiver: WifiReceiver
    var mAppEventCollector: AppEventCollector
    var mAppDataUsageCollector: NetworkUsageCollector
    var mPhoneStateListener: SignalChangeListener
    var locationCollector : LocationListener
    var mLocationManager: LocationManager? = DeviceDetailsApplication.instance.getSystemService(LOCATION_SERVICE) as LocationManager?
    lateinit var timer: Timer

    init{
        mBatteryReceiver = BatteryReceiver()
        mWifiReceiver = WifiReceiver()
        mAppStateReceiver = AppInfoReceiver()
        mAppEventCollector = AppEventCollector(DeviceDetailsApplication.instance)
        mPhoneStateListener = SignalChangeListener(DeviceDetailsApplication.instance)
        mAppDataUsageCollector = NetworkUsageCollector(DeviceDetailsApplication.instance)
        locationCollector = LocationListener(mLocationManager!!,DeviceDetailsApplication.instance)
    }

    fun runTimer(intervalInMinuets: Long) {
        timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
//                    mAppDataUsageCollector.collect()
                    mAppEventCollector.collect()
                }
            },
            0, 1000 * 60 * intervalInMinuets
        )
    }

}