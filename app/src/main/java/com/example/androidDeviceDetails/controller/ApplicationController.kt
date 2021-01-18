package com.example.androidDeviceDetails.controller

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.collectors.*
import java.util.*

class ApplicationController {
    lateinit var timer: Timer

    @RequiresApi(Build.VERSION_CODES.M)
    var instanceMap: MutableMap<String, BaseCollector> = mutableMapOf(
        "BatteryReceiver" to BatteryCollector(),
        "WifiReceiver" to WifiCollector(),
        "AppStateReceiver" to AppInfoCollector(),
        "AppEventCollector" to AppEventCollector(DeviceDetailsApplication.instance),
        "SignalChangeListener" to SignalChangeCollector(),
        "NetworkUsageCollector" to NetworkUsageCollector(DeviceDetailsApplication.instance),
        "LocationCollector" to LocationCollector(DeviceDetailsApplication.instance)
    )

    fun runTimer(intervalInMinuets: Long) {
        timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    for (collector in instanceMap.values) {
                        collector.collect()
                    }
                }
            },
            0, 1000 * 60 * intervalInMinuets
        )
    }

}