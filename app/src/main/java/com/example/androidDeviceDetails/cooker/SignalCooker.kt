package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.signal.SignalCookedData
import com.example.androidDeviceDetails.models.signal.SignalGraphEntry
import com.example.androidDeviceDetails.models.signal.Usage
import com.example.androidDeviceDetails.utils.Signal
import com.example.androidDeviceDetails.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Implements [BaseCooker].
 * A cooker class for handling the logic for cooking signal data.
 **/
class SignalCooker : BaseCooker() {
    private var db: RoomDB = RoomDB.getDatabase()!!

    companion object {
        const val MAX_PLOT_POINTS: Int = 40
        const val MINUTE: Long = 60 * 1000
        const val LEVEL_POOR = 0
        const val LEVEL_LOW = 1
        const val LEVEL_MEDIUM = 2
        const val LEVEL_GOOD = 3
        const val LEVEL_EXCELLENT = 4
    }

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
            val graphEntryList = arrayListOf<SignalGraphEntry>()
            var cellularBandList = ArrayList<Usage>()
            var wifiLevelList = ArrayList<Usage>()
            var carrierNameList = ArrayList<Usage>()
            var ssidList = ArrayList<Usage>()
            val cookedDataList = ArrayList<Any>()
            var roamingTime: Long = 0
            var startTime: Long
            var endTime: Long
            var timeInterval: Long

            val signalRawList = db.signalDao().getAllSignalBetween(time.startTime, time.endTime)
            val cellularList = signalRawList.filter { it.signal == Signal.CELLULAR.ordinal }
            val wifiList = signalRawList.filter { it.signal == Signal.WIFI.ordinal }
            val signalCookedData = SignalCookedData()

            if (cellularList.isNotEmpty()) {
                val lastCellularEntity = cellularList.last()
                var previousCellularEntity = cellularList.first()
                startTime = previousCellularEntity.timeStamp
                endTime = lastCellularEntity.timeStamp
                timeInterval = maxOf((endTime - startTime) / MAX_PLOT_POINTS, MINUTE)

                cellularList.forEach { cellularEntity ->

                    cellularBandList = getMostUsed(
                        cellularBandList,
                        cellularEntity.band!!,
                        previousCellularEntity.timeStamp,
                        cellularEntity.timeStamp
                    )

                    carrierNameList = getMostUsed(
                        carrierNameList,
                        cellularEntity.operatorName,
                        previousCellularEntity.timeStamp,
                        cellularEntity.timeStamp
                    )

                    if (cellularEntity.isRoaming == true)
                        roamingTime += cellularEntity.timeStamp - previousCellularEntity.timeStamp

                    if (cellularEntity.timeStamp >= startTime) {
                        graphEntryList.add(
                            SignalGraphEntry(
                                Utils.getDateTime(cellularEntity.timeStamp),
                                cellularEntity.signal,
                                cellularEntity.strength
                            )
                        )
                        startTime = (timeInterval + cellularEntity.timeStamp)
                    }
                    previousCellularEntity = cellularEntity
                }
                signalCookedData.lastCellularStrength = lastCellularEntity.strengthPercentage
                signalCookedData.mostUsedOperator = carrierNameList.last().name
                signalCookedData.mostUsedCellularBand = cellularBandList.last().name
                signalCookedData.roamingTime = Utils.getTimePeriod(roamingTime)
            }
            if (wifiList.isNotEmpty()) {
                var previousWifiEntity = wifiList.first()
                val lastWifiEntity = wifiList.last()

                startTime = previousWifiEntity.timeStamp
                endTime = lastWifiEntity.timeStamp
                timeInterval = maxOf((endTime - startTime) / MAX_PLOT_POINTS, MINUTE)

                wifiList.forEach { wifiEntity ->
                    wifiLevelList = getMostUsed(
                        wifiLevelList,
                        wifiEntity.level.toString(),
                        previousWifiEntity.timeStamp,
                        wifiEntity.timeStamp
                    )

                    ssidList = getMostUsed(
                        ssidList,
                        wifiEntity.operatorName,
                        previousWifiEntity.timeStamp,
                        wifiEntity.timeStamp
                    )

                    if (wifiEntity.timeStamp >= startTime) {
                        graphEntryList.add(
                            SignalGraphEntry(
                                Utils.getDateTime(wifiEntity.timeStamp),
                                wifiEntity.signal,
                                wifiEntity.strength
                            )
                        )
                        startTime = (timeInterval + wifiEntity.timeStamp)
                    }
                    previousWifiEntity = wifiEntity
                }

                wifiLevelList.sortBy { it.time }
                ssidList.sortBy { it.time }
                signalCookedData.lastWifiStrength = lastWifiEntity.strengthPercentage
                signalCookedData.mostUsedWifi = ssidList.last().name
                signalCookedData.mostUsedWifiLevel = getWifiSpeed(wifiLevelList.last().name.toInt())
            }

            cookedDataList.add(signalCookedData)
            cookedDataList.add(graphEntryList)
            @Suppress("UNCHECKED_CAST")
            callback.onDone(cookedDataList as ArrayList<T>)
        }
    }

    private fun getMostUsed(
        usageList: ArrayList<Usage>, dataValue: String, previousTime: Long, currentTime: Long
    ): ArrayList<Usage> {
        if (usageList.none { it.name == dataValue })
            usageList.add(Usage(dataValue, 0))
        usageList.first { it.name == dataValue }.time += (currentTime - previousTime)
        return usageList
    }

    private fun getWifiSpeed(level: Int): String {
        return when (level) {
            LEVEL_POOR -> "Poor"
            LEVEL_LOW -> "Low"
            LEVEL_MEDIUM -> "Medium"
            LEVEL_GOOD -> "Good"
            LEVEL_EXCELLENT -> "Excellent"
            else -> "Unknown"
        }
    }
}