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
        permissionList.addAll(listOf("PHONE","CALL LOGS","CONTACTS","SMS",
                "LOCATION",
                "CAMERA","MICROPHONE","STORAGE","CALENDER","BODY SENSORS","PHYSICAL ACTIVITY"))
            callback.onDone(permissionList as ArrayList<T>)
    }
}