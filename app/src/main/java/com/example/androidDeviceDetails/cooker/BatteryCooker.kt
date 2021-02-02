package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.battery.BatteryAppEntry
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
     * @param iCookingDone A callback that accepts the cooked list once cooking is done
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        GlobalScope.launch {
            val appEntryMap = mutableMapOf<String, BatteryAppEntry>()
            val db: RoomDB = RoomDB.getDatabase()!!
            val appEventList = db.appEventDao().getAllBetween(time.startTime, time.endTime)
            val batteryList = db.batteryDao().getAllBetween(time.startTime, time.endTime)

            if (batteryList.isNotEmpty() && appEventList.isNotEmpty()) {
                val batteryIterator = batteryList.iterator()
                var batteryInfo = batteryList.first()
                var previousLevel = batteryList.first().level
                var previousApp = appEventList.first().packageName

                for (appEvent in appEventList) {
                    while ((appEvent.timeStamp > batteryInfo.timeStamp || batteryInfo.health == 0) && batteryIterator.hasNext())
                        batteryInfo = batteryIterator.next()
                    getAggregate(previousLevel, previousApp, batteryInfo.level, appEntryMap)
                    previousApp = appEvent.packageName
                    previousLevel = batteryInfo.level
                }
                iCookingDone.onComplete(ArrayList(appEntryMap.values) as ArrayList<T>)
            } else iCookingDone.onComplete(arrayListOf())
        }
    }

    private fun getAggregate(
        previousLevel: Int, previousApp: String, currentLevel: Int,
        appEntryMap: MutableMap<String, BatteryAppEntry>
    ) {
        if (previousLevel > currentLevel) {
            val drop = previousLevel - currentLevel
            if (appEntryMap.containsKey(previousApp))
                appEntryMap[previousApp]!!.drop += drop
            else appEntryMap[previousApp] = BatteryAppEntry(previousApp, drop)
        }
    }
}