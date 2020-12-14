package com.example.androidDeviceDetails.managers

import android.content.Context
import android.widget.ListView
import com.example.androidDeviceDetails.BatteryListAdapter
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.AppUsageModel
import com.example.androidDeviceDetails.models.BatteryRawModel
import com.example.androidDeviceDetails.models.RoomDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppBatteryUsageManager {
    private var db: RoomDB = RoomDB.getDatabase()!!

    private fun getCombinedList(
        appEventList: List<AppUsageModel>,
        batteryList: List<BatteryRawModel>
    ): MutableList<MergedEventData> {
        val mergedList = mutableListOf<MergedEventData>()
        for (it in appEventList)
            mergedList.add(
                MergedEventData(
                    it.timeStamp,
                    -1,
                    -1,
                    it.packageName,
                )
            )
        for (it in batteryList)
            mergedList.add(MergedEventData(it.timeStamp, it.level, it.plugged, " "))
        mergedList.sortBy { it.timestamp }
        var fillerLevel = batteryList[0].level
        var fillerPlugged = batteryList[0].plugged
        mergedList.forEach {
            if (it.batteryLevel == -1) {
                it.batteryLevel = fillerLevel
                it.plugged = fillerPlugged
            }
            fillerLevel = it.batteryLevel
            fillerPlugged = it.plugged
        }
        mergedList.removeAll { it.packageName == " " }
        return mergedList
    }

    fun cookBatteryData(
        context: Context,
        batteryListView: ListView,
        beginTime: Long,
        endTime: Long = System.currentTimeMillis()
    ) {
        val appEntryList = arrayListOf<AppEntry>()
        GlobalScope.launch {
            val appEventList = db.appUsageInfoDao().getAllBetween(beginTime, endTime)
            val batteryList = db.batteryInfoDao().getAllBetween(beginTime, endTime)

            val mergedList = getCombinedList(appEventList, batteryList)
            var previousData = mergedList.first()
            for ((i, mergedEventData) in mergedList.withIndex()) {
                if (mergedEventData.plugged == 0 && previousData.batteryLevel!! > mergedEventData.batteryLevel!!)
                    if (appEntryList.none { it.packageId == mergedEventData.packageName })
                        appEntryList.add(
                            AppEntry(
                                mergedEventData.packageName,
                                previousData.batteryLevel!!.minus(mergedEventData.batteryLevel!!)
                            )
                        )
                    else appEntryList.first { it.packageId == mergedList[i - 1].packageName }.drop +=
                        previousData.batteryLevel!!.minus(mergedEventData.batteryLevel!!)
                previousData = mergedEventData
            }
            batteryListView.post {
                batteryListView.adapter = BatteryListAdapter(context, R.layout.battery_tile, appEntryList)
            }
        }
    }
}

data class MergedEventData(
    var timestamp: Long,
    var batteryLevel: Int?,
    var plugged: Int?,
    var packageName: String,
)

data class AppEntry(var packageId: String, var drop: Int = 0)