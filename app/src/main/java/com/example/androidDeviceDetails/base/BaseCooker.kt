package com.example.androidDeviceDetails.base

import android.location.Location
import com.example.androidDeviceDetails.cooker.*
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.ui.*

abstract class BaseCooker {
    abstract fun <T> cook(time: TimePeriod, callback: ICookingDone<T>)

    companion object {
        fun getCooker(type: String): BaseCooker? {
            return when (type) {
                MainActivity.NAME->MainActivityCooker()
                BatteryActivity.NAME -> BatteryCooker()
                AppInfoActivity.NAME -> AppInfoCooker()
                NetworkUsageActivity.NAME -> AppNetworkUsageCooker()
                SignalActivity.NAME -> SignalCooker()
                LocationActivity.NAME -> LocationCooker()
                AppTypeActivity.NAME -> AppTypeCooker()
                PermissionsActivity.NAME -> PermissionsCooker()
                else -> null
            }
        }
    }
}