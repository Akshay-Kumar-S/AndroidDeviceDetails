package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.database.SignalRaw
import com.example.androidDeviceDetails.models.signal.SignalCookedData
import com.example.androidDeviceDetails.models.signal.SignalGraphEntry
import com.example.androidDeviceDetails.utils.Signal
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
            val cellularBandList = mutableMapOf<String, Long>()
            val wifiLevelList = mutableMapOf<String, Long>()
            val carrierNameList = mutableMapOf<String, Long>()
            val ssidList = mutableMapOf<String, Long>()
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
                val lastCellularRaw = cellularList.last()
                var previousCellularEntity = cellularList.first()
                startTime = previousCellularEntity.timeStamp
                endTime = lastCellularRaw.timeStamp
                timeInterval = maxOf((endTime - startTime) / MAX_PLOT_POINTS, MINUTE)

                cellularList.forEach { cellularEntity ->

                    getMostUsed(
                        cellularEntity.band!!,
                        cellularBandList,
                        previousCellularEntity.timeStamp,
                        cellularEntity.timeStamp
                    )

                    getMostUsed(
                        cellularEntity.operatorName,
                        carrierNameList,
                        previousCellularEntity.timeStamp,
                        cellularEntity.timeStamp
                    )

                    if (cellularEntity.isRoaming)
                        signalCookedData.roamingTime += cellularEntity.timeStamp - previousCellularEntity.timeStamp

                    if (cellularEntity.timeStamp >= startTime) {
                        cookedDataList.add(
                            SignalGraphEntry(
                                cellularEntity.timeStamp,
                                cellularEntity.signal,
                                cellularEntity.strength
                            )
                        )
                        startTime = (timeInterval + cellularEntity.timeStamp)
                    }
                    previousCellularEntity = cellularEntity
                }
                signalCookedData.lastCellularStrength = lastCellularEntity.strengthPercentage
                signalCookedData.mostUsedOperator = carrierNameList.maxByOrNull { it.value }!!.key
                signalCookedData.mostUsedCellularBand =
                    cellularBandList.maxByOrNull { it.value }!!.key
            }
            if (wifiList.isNotEmpty()) {
                var previousWifiEntity = wifiList.first()
                val lastWifiEntity = wifiList.last()

                startTime = previousWifiEntity.timeStamp
                endTime = lastWifiEntity.timeStamp
                timeInterval = maxOf((endTime - startTime) / MAX_PLOT_POINTS, MINUTE)

                wifiList.forEach { wifiEntity ->
                    getMostUsed(
                        previousWifiEntity.level,
                        wifiLevelList,
                        previousWifiEntity.timeStamp,
                        wifiEntity.timeStamp
                    )

                    getMostUsed(
                        previousWifiEntity.operatorName,
                        ssidList,
                        previousWifiEntity.timeStamp,
                        wifiEntity.timeStamp
                    )

                    if (wifiEntity.timeStamp >= startTime) {
                        cookedDataList.add(
                            SignalGraphEntry(
                                wifiEntity.timeStamp,
                                wifiEntity.signal,
                                wifiEntity.strength
                            )
                        )
                        startTime = (timeInterval + wifiEntity.timeStamp)
                    }
                    previousWifiEntity = wifiEntity
                }
                signalCookedData.mostUsedWifi = ssidList.maxByOrNull { it.value }!!.key
                signalCookedData.lastWifiStrength = lastWifiEntity.strengthPercentage
                //Log.d("zzz", "cook: $wifiLevelList")
                signalCookedData.mostUsedWifiLevel = wifiLevelList.maxByOrNull { it.value }!!.key
                // Log.d("zzz", "cook: ${signalCookedData.mostUsedWifiLevel}")

            }

            cookedDataList.add(signalCookedData)
            callback.onDone(cookedDataList as ArrayList<T>)
        }
    }

    private fun getMostUsed(
        key: String, usageMap: MutableMap<String, Long>, previousTime: Long, currentTime: Long
    ): MutableMap<String, Long> {
        if (!usageMap.containsKey(key))
            usageMap[key] = 0
        usageMap[key] = usageMap.getValue(key) + (currentTime - previousTime)
        return usageMap
    }
}