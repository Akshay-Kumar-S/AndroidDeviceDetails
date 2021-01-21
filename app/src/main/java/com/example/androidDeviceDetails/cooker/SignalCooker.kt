package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.signalModels.SignalCookedData
import com.example.androidDeviceDetails.models.signalModels.SignalRaw
import com.example.androidDeviceDetails.models.signalModels.Usage
import com.example.androidDeviceDetails.utils.Signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Implements [BaseCooker].
 * A cooker class for handling the logic for cooking signal data.
 **/
class SignalCooker : BaseCooker() {
    private var db: RoomDB = RoomDB.getDatabase()!!

    /**
     * Cook data for Signal Strength from the collected data available in the [RoomDB.signalDao]
     * table for the requested time interval.
     * >
     * Overrides : [cook] in [BaseCooker]
     * @param time data class object that contains start time and end time.
     * @param callback A callback that accepts the cooked list once cooking is done
     */
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        GlobalScope.launch {
            val cellularList =
                db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.CELLULAR.ordinal)
            val wifiList =
                db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.WIFI.ordinal)
            var lastCellStrength=cellularList.last().strength
            var lastWifiStrength=wifiList.last().strength
            var roamingTime: Long = roamingTime(cellularList)
            val cookedDataList = ArrayList<SignalCookedData>()
            val cookedData = SignalCookedData(
                roamingTime,
                getMostUsed(cellularList, "operator"),
                getMostUsed(wifiList, "operator"),
                getMostUsed(cellularList, "band"),
                getMostUsed(wifiList, "level"),
                lastWifiStrength,
                lastCellStrength
            )
            cookedDataList.add(cookedData)
            if (cellularList.isNotEmpty()) {
                callback.onDone(cookedDataList as ArrayList<T>)
            } else callback.onDone(arrayListOf())
        }
    }

    private fun getMostUsed(
        rawList: List<SignalRaw>,
        data: String,
    ): String {
        if (rawList.isEmpty()) return "no data"
        val usageList = ArrayList<Usage>()
        var dataValue: String
        var previousSignalEntity = rawList.first()
        rawList.forEach { signalEntity ->
            dataValue = when (data) {
                "band" -> signalEntity.band.toString()
                "operator" -> signalEntity.operatorName
                "level" -> signalEntity.level.toString()
                else -> signalEntity.operatorName
            }
            if (usageList.none { it.name == dataValue })
                usageList.add(Usage(dataValue, 0))
            usageList.first { it.name == dataValue }.time += (signalEntity.timeStamp - previousSignalEntity.timeStamp)
            previousSignalEntity = signalEntity
        }
        usageList.sortBy { it.time }
        return usageList.last().name.toString()
    }

    private fun roamingTime(cellularList: List<SignalRaw>): Long {
        if (cellularList.isEmpty()) return 0
        var roamingTime: Long = 0
        var previousSignalEntity = cellularList.first()
        for (i in cellularList) {
            if (i.isRoaming == true) roamingTime += i.timeStamp - previousSignalEntity.timeStamp
            previousSignalEntity = i
        }
        return roamingTime
    }
}

