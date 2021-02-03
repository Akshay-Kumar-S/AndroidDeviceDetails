package com.example.androidDeviceDetails.base

import com.example.androidDeviceDetails.cooker.*
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.ui.*

abstract class BaseCooker {
    abstract fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>)

    companion object {
        fun getCooker(type: String): BaseCooker? {
            return when (type) {
                MainActivity.NAME->MainActivityCooker()
                BatteryActivity.NAME -> BatteryCooker()
                AppEventActivity.NAME -> AppEventCooker()
                NetworkUsageActivity.NAME -> AppNetworkUsageCooker()
                SignalActivity.NAME -> SignalCooker()
                LocationActivity.NAME -> LocationCooker()
                AppTypeActivity.NAME -> AppTypeCooker()
                AppPermissionsActivity.NAME -> PermissionsCooker()
                else -> null
            }
        }
    }
}