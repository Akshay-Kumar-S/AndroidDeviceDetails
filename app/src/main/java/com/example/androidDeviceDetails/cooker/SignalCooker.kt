package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.database.SignalRaw
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.signal.Signal
import com.example.androidDeviceDetails.models.signal.Signal.GRAPH_PLOT_POINTS
import com.example.androidDeviceDetails.models.signal.SignalCookedData
import com.example.androidDeviceDetails.models.signal.SignalGraphEntry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Implements [BaseCooker].
 * A cooker class for handling the logic for cooking signal data.
 **/
@Suppress("UNCHECKED_CAST")
class SignalCooker : BaseCooker() {
    private var db: RoomDB = RoomDB.getDatabase()!!

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
            val signalCookedData = SignalCookedData()
            val signalRawList = db.signalDao().getAll(time.startTime, time.endTime)

            signalRawList.partition { it.signal == Signal.CELLULAR }.apply {
                cookCellularData(first as ArrayList<SignalRaw>, signalCookedData)
                cookWifiData(second as ArrayList<SignalRaw>, signalCookedData)
            }

            iCookingDone.onComplete(arrayListOf(signalCookedData) as ArrayList<T>)
        }
    }

    /**
     *The function finds aggregate time for each distinct Entry.
     */
    private fun aggregateMostUsed(
        key: String, usageMap: MutableMap<String, Long>, timeInterval: Long
    ) {
        if (!usageMap.containsKey(key))
            usageMap[key] = 0
        usageMap[key] = usageMap.getValue(key) + timeInterval
    }

    private fun cookCellularData(
        cellularList: ArrayList<SignalRaw>, signalCookedData: SignalCookedData
    ) {
        val cellularBandMap = mutableMapOf<String, Long>()
        val carrierNameMap = mutableMapOf<String, Long>()
        var timeInterval: Long

        if (cellularList.isNotEmpty()) {
            var prevCellularRaw = cellularList.first()
            val lastCellularRaw = cellularList.last()
            var startTime = prevCellularRaw.timeStamp
            val endTime = lastCellularRaw.timeStamp
            val graphTimeInterval =
                maxOf((endTime - startTime) / GRAPH_PLOT_POINTS, TimeUnit.MINUTES.toMillis(1))

            cellularList.forEach { cellularRaw ->
                timeInterval = cellularRaw.timeStamp - prevCellularRaw.timeStamp

                aggregateMostUsed(prevCellularRaw.band!!, cellularBandMap, timeInterval)
                aggregateMostUsed(prevCellularRaw.operatorName, carrierNameMap, timeInterval)

                if (prevCellularRaw.isRoaming)
                    signalCookedData.roamingTime += timeInterval

                if (cellularRaw.timeStamp >= startTime) {
                    updateGraphEntry(cellularRaw, signalCookedData)
                    startTime = (graphTimeInterval + cellularRaw.timeStamp)
                }
                prevCellularRaw = cellularRaw
            }
            signalCookedData.lastCellularStrength = lastCellularRaw.strengthPercentage
            signalCookedData.mostUsedOperator = carrierNameMap.maxByOrNull { it.value }!!.key
            signalCookedData.mostUsedCellularBand =
                cellularBandMap.maxByOrNull { it.value }!!.key
        }
    }

    private fun cookWifiData(wifiList: ArrayList<SignalRaw>, signalCookedData: SignalCookedData) {
        val wifiLevelMap = mutableMapOf<String, Long>()
        val ssidMap = mutableMapOf<String, Long>()
        var timeInterval: Long

        if (wifiList.isNotEmpty()) {
            var prevWifiRaw = wifiList.first()
            val lastWifiRaw = wifiList.last()
            var startTime = prevWifiRaw.timeStamp
            val endTime = lastWifiRaw.timeStamp
            val graphTimeInterval =
                maxOf((endTime - startTime) / GRAPH_PLOT_POINTS, TimeUnit.MINUTES.toMillis(1))

            wifiList.forEach { wifiRaw ->
                timeInterval = wifiRaw.timeStamp - prevWifiRaw.timeStamp

                aggregateMostUsed(prevWifiRaw.level, wifiLevelMap, timeInterval)
                aggregateMostUsed(prevWifiRaw.operatorName, ssidMap, timeInterval)

                if (wifiRaw.timeStamp >= startTime) {
                    updateGraphEntry(wifiRaw, signalCookedData)
                    startTime = (graphTimeInterval + wifiRaw.timeStamp)
                }
                prevWifiRaw = wifiRaw
            }
            signalCookedData.lastWifiStrength = lastWifiRaw.strengthPercentage
            signalCookedData.mostUsedWifi = ssidMap.maxByOrNull { it.value }!!.key
            signalCookedData.mostUsedWifiLevel = wifiLevelMap.maxByOrNull { it.value }!!.key
        }
    }

    private fun updateGraphEntry(signalRaw: SignalRaw, signalCookedData: SignalCookedData) {
        signalCookedData.garphEntryList.add(
            SignalGraphEntry(signalRaw.timeStamp, signalRaw.signal, signalRaw.strength)
        )
    }
}