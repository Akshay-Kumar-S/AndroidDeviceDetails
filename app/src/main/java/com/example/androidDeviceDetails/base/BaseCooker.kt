package com.example.androidDeviceDetails.base

import android.location.Location
import com.example.androidDeviceDetails.cooker.*
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.ui.*
import com.example.androidDeviceDetails.viewModel.PermittedAppsViewModel

abstract class BaseCooker {
    abstract fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>)

    companion object {
        fun getCooker(type: String): BaseCooker? {
            if(type.startsWith(PermittedAppsActivity.NAME))
                return PermittedAppsCooker(type)
            return when (type) {
                MainActivity.NAME->MainActivityCooker()
                BatteryActivity.NAME -> BatteryCooker()
                AppEventActivity.NAME -> AppEventCooker()
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