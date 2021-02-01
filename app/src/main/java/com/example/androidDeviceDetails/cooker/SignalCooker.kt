package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.database.SignalRaw
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.signal.SignalCookedData
import com.example.androidDeviceDetails.models.signal.SignalGraphEntry
import com.example.androidDeviceDetails.models.signal.Signal
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
        const val GRAPH_PLOT_POINTS: Int = 40
        const val MINUTE: Long = 60 * 1000
    }

    /**
     * Cook data for Signal Strength from the collected data available in the [RoomDB.signalDao]
     * table for the requested time interval.
     * >
     * Overrides : [cook] in [BaseCooker]
     * @param time data class object that contains start time and end time.
     * @param iCookingDone A callback that accepts the cooked list once cooking is done
     */
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        GlobalScope.launch {
            val cookedDataList = ArrayList<Any>()
            val cellularList = arrayListOf<SignalRaw>()
            val wifiList = arrayListOf<SignalRaw>()
            val signalCookedData = SignalCookedData()

            val signalRawList = db.signalDao().getAllSignalBetween(time.startTime, time.endTime)
            signalRawList.partition { it.signal == Signal.CELLULAR.ordinal }.apply {
                cookCellularData(first as ArrayList<SignalRaw>, signalCookedData, cookedDataList)
                cookWifiData(second as ArrayList<SignalRaw>, signalCookedData, cookedDataList)
            }
            cookCellularData(cellularList, signalCookedData, cookedDataList)
            cookWifiData(wifiList, signalCookedData, cookedDataList)
            cookedDataList.add(signalCookedData)
            iCookingDone.onComplete(cookedDataList as ArrayList<T>)
        }
    }

    private fun aggregateMostUsed(
        key: String, usageMap: MutableMap<String, Long>, previousTime: Long, currentTime: Long
    ) {
        if (!usageMap.containsKey(key))
            usageMap[key] = 0
        usageMap[key] = usageMap.getValue(key) + (currentTime - previousTime)
    }

    private fun cookCellularData(
        cellularList: ArrayList<SignalRaw>,
        signalCookedData: SignalCookedData,
        cookedDataList: ArrayList<Any>
    ) {
        val cellularBandMap = mutableMapOf<String, Long>()
        val carrierNameMap = mutableMapOf<String, Long>()
        var startTime: Long
        val endTime: Long
        val timeInterval: Long

        if (cellularList.isNotEmpty()) {
            val lastCellularRaw = cellularList.last()
            var prevCellularRaw = cellularList.first()
            startTime = prevCellularRaw.timeStamp
            endTime = lastCellularRaw.timeStamp
            timeInterval = maxOf((endTime - startTime) / GRAPH_PLOT_POINTS, MINUTE)

            cellularList.forEach { cellularRaw ->
                aggregateMostUsed(
                    prevCellularRaw.band!!, cellularBandMap, prevCellularRaw.timeStamp,
                    cellularRaw.timeStamp
                )

                aggregateMostUsed(
                    prevCellularRaw.operatorName, carrierNameMap, prevCellularRaw.timeStamp,
                    cellularRaw.timeStamp
                )

                if (prevCellularRaw.isRoaming)
                    signalCookedData.roamingTime += cellularRaw.timeStamp - prevCellularRaw.timeStamp

                if (cellularRaw.timeStamp >= startTime) {
                    cookedDataList.add(
                        SignalGraphEntry(
                            cellularRaw.timeStamp, cellularRaw.signal, cellularRaw.strength
                        )
                    )
                    startTime = (timeInterval + cellularRaw.timeStamp)
                }
                prevCellularRaw = cellularRaw
            }
            signalCookedData.lastCellularStrength = lastCellularRaw.strengthPercentage
            signalCookedData.mostUsedOperator = carrierNameMap.maxByOrNull { it.value }!!.key
            signalCookedData.mostUsedCellularBand =
                cellularBandMap.maxByOrNull { it.value }!!.key
        }
    }

    private fun cookWifiData(
        wifiList: ArrayList<SignalRaw>,
        signalCookedData: SignalCookedData,
        cookedDataList: ArrayList<Any>
    ) {
        val wifiLevelMap = mutableMapOf<String, Long>()
        val ssidMap = mutableMapOf<String, Long>()
        var startTime: Long
        val endTime: Long
        val timeInterval: Long

        if (wifiList.isNotEmpty()) {
            var prevWifiRaw = wifiList.first()
            val lastWifiRaw = wifiList.last()

            startTime = prevWifiRaw.timeStamp
            endTime = lastWifiRaw.timeStamp
            timeInterval = maxOf((endTime - startTime) / GRAPH_PLOT_POINTS, MINUTE)

            wifiList.forEach { wifiRaw ->
                aggregateMostUsed(
                    prevWifiRaw.level, wifiLevelMap, prevWifiRaw.timeStamp, wifiRaw.timeStamp
                )

                aggregateMostUsed(
                    prevWifiRaw.operatorName, ssidMap, prevWifiRaw.timeStamp, wifiRaw.timeStamp
                )

                if (wifiRaw.timeStamp >= startTime) {
                    cookedDataList.add(
                        SignalGraphEntry(
                            wifiRaw.timeStamp, wifiRaw.signal, wifiRaw.strength
                        )
                    )
                    startTime = (timeInterval + wifiRaw.timeStamp)
                }
                prevWifiRaw = wifiRaw
            }
            signalCookedData.lastWifiStrength = lastWifiRaw.strengthPercentage
            signalCookedData.mostUsedWifi = ssidMap.maxByOrNull { it.value }!!.key
            signalCookedData.mostUsedWifiLevel = wifiLevelMap.maxByOrNull { it.value }!!.key
        }
    }
}