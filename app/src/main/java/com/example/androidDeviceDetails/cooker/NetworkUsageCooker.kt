package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.ICookingDone
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class NetworkUsageCooker : BaseCooker() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimeInterval, callback: ICookingDone<T>) {
        val db = RoomDB.getDatabase()?.appNetworkUsageDao()!!
        GlobalScope.launch {
            val inBetweenList = db.getAllBetween(time.startTime, time.endTime)
            if (inBetweenList.isNotEmpty()) {
                val firstElementTime = inBetweenList.first().timeStamp
                val initialAppDataList = inBetweenList.filter { it.timeStamp == firstElementTime }
                val lastElementTime = inBetweenList.last().timeStamp
                val finalAppDataList = inBetweenList.filter { it.timeStamp == lastElementTime }
                val totalDataUsageList = arrayListOf<AppNetworkUsageEntity>()
                finalAppDataList.forEach {
                    val nullCheckList =
                        initialAppDataList.filter { appDataUsage -> it.packageName == appDataUsage.packageName } //To filter out common apps in initial and final list.
                    if (nullCheckList.isNotEmpty()) {
                        val initialAppData = nullCheckList[0]
                        totalDataUsageList.add(
                            AppNetworkUsageEntity(
                                0,
                                it.timeStamp,
                                it.packageName,
                                it.transferredDataWifi - initialAppData.transferredDataWifi,
                                it.transferredDataMobile - initialAppData.transferredDataMobile,
                                it.receivedDataWifi - initialAppData.receivedDataWifi,
                                it.receivedDataMobile - initialAppData.receivedDataMobile
                            )
                        )
                    } else totalDataUsageList.add(it)
                }
                callback.onDone(totalDataUsageList as ArrayList<T>)
            } else callback.onDone(arrayListOf())

        }
    }

}