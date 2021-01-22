package com.example.androidDeviceDetails.base

import com.example.androidDeviceDetails.cooker.*
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.ui.*

abstract class BaseCooker {
    abstract fun <T> cook(time: TimePeriod, callback: ICookingDone<T>, subCookers: List<String>? = null)

    companion object {
        fun getCooker(type: String): BaseCooker? {
            return when (type) {
                BatteryActivity.NAME -> BatteryCooker()
                AppInfoActivity.NAME -> AppInfoParentCooker()
                NetworkUsageActivity.NAME -> NetworkUsageCooker()
                SignalActivity.NAME -> SignalCooker()
                LocationActivity.NAME -> LocationCooker()
                else -> null
            }
        }
    }
}