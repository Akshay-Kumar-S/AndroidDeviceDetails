package com.example.androidDeviceDetails.base

import com.example.androidDeviceDetails.ICookingDone
import com.example.androidDeviceDetails.activities.AppInfoActivity
import com.example.androidDeviceDetails.activities.BatteryActivity
import com.example.androidDeviceDetails.activities.LocationActivity
import com.example.androidDeviceDetails.cooker.AppInfoCooker
import com.example.androidDeviceDetails.cooker.BatteryCooker
import com.example.androidDeviceDetails.cooker.LocationCooker
import com.example.androidDeviceDetails.cooker.NetworkUsageCooker
import com.example.androidDeviceDetails.models.TimeInterval

abstract class BaseCooker {
    abstract fun <T> cook(time: TimeInterval, callback: ICookingDone<T>)

    companion object {
        fun getCooker(type: String): BaseCooker {
            return when (type) {
                BatteryActivity.NAME -> BatteryCooker()
                AppInfoActivity.NAME -> AppInfoCooker()
                LocationActivity.NAME -> LocationCooker()
                else -> NetworkUsageCooker()
            }
        }
    }
}
