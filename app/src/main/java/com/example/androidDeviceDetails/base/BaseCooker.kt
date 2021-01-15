package com.example.androidDeviceDetails.base

import com.example.androidDeviceDetails.activities.AppInfoActivity
import com.example.androidDeviceDetails.activities.BatteryActivity
import com.example.androidDeviceDetails.activities.SignalActivity
import com.example.androidDeviceDetails.cooker.AppInfoCooker
import com.example.androidDeviceDetails.cooker.BatteryCooker
import com.example.androidDeviceDetails.cooker.NetworkUsageCooker
import com.example.androidDeviceDetails.cooker.SignalCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.ui.AppInfoActivity
import com.example.androidDeviceDetails.ui.BatteryActivity
import com.example.androidDeviceDetails.ui.NetworkUsageActivity

abstract class BaseCooker {
    abstract fun <T> cook(time: TimePeriod, callback: ICookingDone<T>)

    companion object {
        fun getCooker(type: String): BaseCooker? {
            return when (type) {
                BatteryActivity.NAME -> BatteryCooker()
                AppInfoActivity.NAME -> AppInfoCooker()
                NetworkUsageActivity.NAME -> NetworkUsageCooker()
                SignalActivity.NAME -> SignalCooker()
                else -> null
            }
        }
    }
}