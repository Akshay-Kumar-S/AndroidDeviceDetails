package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.appInfoModels.AppHistoryDao
import java.util.*

class PermissionsCooker : BaseCooker() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
            val permissionList = arrayListOf<String>()
        permissionList.addAll(listOf("ACCEPT_HANDOVER",
                "ACCESS_BACKGROUND_LOCATION",
                "ACCESS_CHECKIN_PROPERTIES","ACCESS_COARSE_LOCATION","ACCESS_FINE_LOCATION","ACCESS_LOCATION_EXTRA_COMMANDS"))
            callback.onDone(permissionList as ArrayList<T>)
    }
}