package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.database.SignalRaw
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
@Suppress("UNCHECKED_CAST")
class SignalCooker : BaseCooker() {
    private var db: RoomDB = RoomDB.getDatabase()!!

    companion object {
        const val MAX_PLOT_POINTS: Int = 40
        const val MINUTE: Long = 60 * 1000
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
            var startTime: Long
            var endTime: Long
            var timeInterval: Long
            val cellularList = arrayListOf<SignalRaw>()
            val wifiList = arrayListOf<SignalRaw>()
            val signalCookedData = SignalCookedData()

            val signalRawList = db.signalDao().getAllSignalBetween(time.startTime, time.endTime)
            signalRawList.partition { it.signal == Signal.CELLULAR.ordinal }.apply {
                first.forEach() {
                    cellularList.add(it)
                }
                second.forEach() {
                    wifiList.add(it)
                }
            }

            if (cellularList.isNotEmpty()) {
                val lastCellularEntity = cellularList.last()
                var previousCellularEntity = cellularList.first()
                startTime = previousCellularEntity.timeStamp
                endTime = lastCellularEntity.timeStamp
                timeInterval = maxOf((endTime - startTime) / MAX_PLOT_POINTS, MINUTE)

                cellularList.forEach { cellularEntity ->

                    cellularBandList = getMostUsed(
                        cellularEntity.band!!,
                        cellularBandList,
                        previousCellularEntity.timeStamp,
                        cellularEntity.timeStamp
                    )

                    carrierNameList = getMostUsed(
                        cellularEntity.operatorName,
                        carrierNameList,
                        previousCellularEntity.timeStamp,
                        cellularEntity.timeStamp
                    )

                    if (cellularEntity.isRoaming)
                        signalCookedData.roamingTime += cellularEntity.timeStamp - previousCellularEntity.timeStamp

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
                carrierNameList.sortBy { it.time }
                cellularBandList.sortBy { it.time }
                signalCookedData.lastCellularStrength = lastCellularEntity.strengthPercentage
                signalCookedData.mostUsedOperator = carrierNameList.last().name
                signalCookedData.mostUsedCellularBand = cellularBandList.last().name
            }
            if (wifiList.isNotEmpty()) {
                var previousWifiEntity = wifiList.first()
                val lastWifiEntity = wifiList.last()

                startTime = previousWifiEntity.timeStamp
                endTime = lastWifiEntity.timeStamp
                timeInterval = maxOf((endTime - startTime) / MAX_PLOT_POINTS, MINUTE)

                wifiList.forEach { wifiEntity ->
                    wifiLevelList = getMostUsed(
                        previousWifiEntity.level,
                        wifiLevelList,
                        previousWifiEntity.timeStamp,
                        wifiEntity.timeStamp
                    )

                    ssidList = getMostUsed(
                        previousWifiEntity.operatorName,
                        ssidList,
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
                signalCookedData.mostUsedWifiLevel = wifiLevelList.last().name
            }

            cookedDataList.add(signalCookedData)
            cookedDataList.add(graphEntryList)
            callback.onDone(cookedDataList as ArrayList<T>)
        }
    }

    private fun getMostUsed(
        dataValue: String, usageList: ArrayList<Usage>, previousTime: Long, currentTime: Long
    ): ArrayList<Usage> {
        if (usageList.none { it.name == dataValue })
            usageList.add(Usage(dataValue, 0))
        usageList.first { it.name == dataValue }.time += (currentTime - previousTime)
        return usageList
    }
}