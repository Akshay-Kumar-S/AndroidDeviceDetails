package com.example.androidDeviceDetails.controller

import com.example.androidDeviceDetails.DeviceDetailsApplication
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
    lateinit var timer: Timer

    init{
        mBatteryReceiver = BatteryReceiver()
        mWifiReceiver = WifiReceiver()
        mAppStateReceiver = AppInfoReceiver()
        mAppEventCollector = AppEventCollector(DeviceDetailsApplication.instance)
        mPhoneStateListener = SignalChangeListener(DeviceDetailsApplication.instance)
        mAppDataUsageCollector = NetworkUsageCollector(DeviceDetailsApplication.instance)
    }

    fun runTimer(intervalInMinuets: Long) {
        timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    mAppDataUsageCollector.collect()
                    mAppEventCollector.collect()
                }
            },
            0, 1000 * 60 * intervalInMinuets
        )
    }

}