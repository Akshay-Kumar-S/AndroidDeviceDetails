package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageRaw
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class DeviceNetworkUsageCooker : BaseCooker() {
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        val db = RoomDB.getDatabase()?.deviceNetworkUsageDao()!!
        GlobalScope.launch {
            val inBetweenList = db.getAllBetween(time.startTime, time.endTime)
            if (inBetweenList.isNotEmpty()) {
                val initialData = inBetweenList.first()
                val finalData = inBetweenList.last()
                val totalDataUsageList =
                    arrayListOf<DeviceNetworkUsageRaw>() //To return a list to keep signature of cook common.
                totalDataUsageList.add(
                    DeviceNetworkUsageRaw(
                        System.currentTimeMillis(),
                        finalData.transferredDataWifi - initialData.transferredDataWifi,
                        finalData.transferredDataMobile - initialData.transferredDataMobile,
                        finalData.receivedDataWifi - initialData.receivedDataWifi,
                        finalData.receivedDataMobile - initialData.receivedDataMobile,
                    )
                )
                @Suppress("UNCHECKED_CAST")
                callback.onDone(totalDataUsageList as ArrayList<T>)
            } else callback.onDone(arrayListOf())
        }

    }
}