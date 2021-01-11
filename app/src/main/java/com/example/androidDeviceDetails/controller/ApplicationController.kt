package com.example.androidDeviceDetails.controller

import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.managers.AppEventCollector
import com.example.androidDeviceDetails.managers.NetworkUsageCollector
import com.example.androidDeviceDetails.managers.SignalChangeListener
import com.example.androidDeviceDetails.receivers.AppInfoReceiver
import com.example.androidDeviceDetails.receivers.BatteryReceiver
import com.example.androidDeviceDetails.receivers.WifiReceiver
import java.util.*

class ApplicationController {
    lateinit var timer: Timer
    var instanceMap : MutableMap<String, BaseCollector>

    init{
        instanceMap = mutableMapOf("BatteryReceiver" to BatteryReceiver(),
            "WifiReceiver" to WifiReceiver(),
            "AppStateReceiver" to AppInfoReceiver(),
            "AppEventCollector" to AppEventCollector(DeviceDetailsApplication.instance),
            "SignalChangeListener" to SignalChangeListener(DeviceDetailsApplication.instance),
            "NetworkUsageCollector" to NetworkUsageCollector(DeviceDetailsApplication.instance))
    }

    fun runTimer(intervalInMinuets: Long) {
        timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    for(collector in instanceMap.values) {
                        collector.collect()
                    }
                }
            },
            0, 1000 * 60 * intervalInMinuets
        )
    }

}