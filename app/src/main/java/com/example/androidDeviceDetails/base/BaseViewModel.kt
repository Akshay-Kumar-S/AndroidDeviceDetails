package com.example.androidDeviceDetails.base

import com.example.androidDeviceDetails.battery.BatteryActivity
import com.example.androidDeviceDetails.battery.BatteryAppEntry
import com.example.androidDeviceDetails.battery.BatteryViewModel

abstract class BaseViewModel<T> {
    abstract fun populateList(data: ArrayList<T>)

    companion object {
        fun getViewModel(type: String): BaseViewModel<BatteryAppEntry> {
            if (BatteryActivity.NAME == type) {
                return BatteryViewModel(null, null)
            }
        }
    }
}