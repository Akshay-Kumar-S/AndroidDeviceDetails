package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.ICookingDone
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BatteryCooker {
    private var db: RoomDB = RoomDB.getDatabase()!!

    fun cookBatteryData(
        callback: ICookingDone<BatteryAppEntry>,
        beginTime: Long,
        endTime: Long = System.currentTimeMillis()
    ) {
        val appEntryList = arrayListOf<BatteryAppEntry>()
        GlobalScope.launch {
            val appEventList = db.appEventDao().getAllBetween(beginTime, endTime)
            val batteryList = db.batteryDao().getAllBetween(beginTime, endTime)
            if (batteryList.isNotEmpty() && appEventList.isNotEmpty()) {
                val batteryIterator = batteryList.iterator()
                var batteryInfo = batteryList.first()
                var previousBattery = batteryList.first()
                var previousApp = appEventList.first()
                for (appEvent in appEventList) {
                    while ((appEvent.timeStamp > batteryInfo.timeStamp || batteryInfo.health == 0) && batteryIterator.hasNext())
                        batteryInfo = batteryIterator.next()
                    if (batteryInfo.plugged == 0 && previousBattery.level!! > batteryInfo.level!!)
                        if (appEntryList.none { it.packageId == previousApp.packageName })
                            appEntryList.add(
                                BatteryAppEntry(
                                    previousApp.packageName,
                                    previousBattery.level!!.minus(batteryInfo.level!!)
                                )
                            )
                        else appEntryList.first { it.packageId == previousApp.packageName }.drop +=
                            previousBattery.level!!.minus(batteryInfo.level!!)
                    previousApp = appEvent
                    previousBattery = batteryInfo
                }
                callback.onData(appEntryList)
            } else callback.onNoData()
        }
    }
}

