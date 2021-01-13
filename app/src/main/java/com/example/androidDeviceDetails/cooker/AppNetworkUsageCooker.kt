package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AppNetworkUsageCooker : BaseCooker() {

    @Suppress("UNCHECKED_CAST")
    /**
     * Cook data for Network Usage from the collected data available in the Database for the
     * requested time interval.
     *>
     * Overrides [cook] in [BaseCooker]
     * >
     * @param time A data class object that contains start time and end time.
     * @param callback A callback that accepts the cooked list once the cooking is done.
     */
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
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