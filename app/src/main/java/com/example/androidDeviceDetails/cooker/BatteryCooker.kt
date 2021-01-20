package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.battery.BatteryAppEntry
import com.example.androidDeviceDetails.models.database.RoomDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Implements [BaseCooker]. A cooker class for handling the logic for cooking for battery data.
 **/
class BatteryCooker : BaseCooker() {

    /**
     * Cook data for App Info from the collected data available in the [RoomDB.appEventDao] and [RoomDB.batteryDao] table for
     * the requested time interval.
     * >
     * Overrides : [cook] in [BaseCooker]
     * @param time data class object that contains start time and end time.
     * @param callback A callback that accepts the cooked list once cooking is done
     */
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        GlobalScope.launch {
            val appEntryList = arrayListOf<BatteryAppEntry>()
            val db: RoomDB = RoomDB.getDatabase()!!
            val appEventList = db.appEventDao().getAllBetween(time.startTime, time.endTime)
            val batteryList = db.batteryDao().getAllBetween(time.startTime, time.endTime)

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
                @Suppress("UNCHECKED_CAST")
                callback.onDone(appEntryList as ArrayList<T>)
            } else callback.onDone(arrayListOf())
        }
    }

}

