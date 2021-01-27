package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.database.SignalRaw
import com.example.androidDeviceDetails.models.signal.SignalCookedData
import com.example.androidDeviceDetails.models.signal.SignalEntry
import com.example.androidDeviceDetails.models.signal.Usage
import com.example.androidDeviceDetails.utils.Signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Implements [BaseCooker].
 * A cooker class for handling the logic for cooking signal data.
 **/
class SignalCooker : BaseCooker() {
    private var db: RoomDB = RoomDB.getDatabase()!!
    private val signalList = arrayListOf<SignalEntry>()

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
    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        GlobalScope.launch {
            var wifiPercentage = 0F
            var cellularPercentage = 0F
            var cellularBandList = ArrayList<Usage>()
            var wifiLevelList = ArrayList<Usage>()
            var cellularNameList = ArrayList<Usage>()
            var wifiNameList = ArrayList<Usage>()
            var previousCellularEntity: SignalRaw
            var roamingTime: Long = 0
            var startTime: Long
            var endTime: Long
            var timeStamp: String
            var timeDifference:Long
            var timeInterval:Long
            val formatter = SimpleDateFormat("HH:mm dd MMM yyyy", Locale.ENGLISH)

            val signalRawList = db.signalDao().getAllSignalBetween(time.startTime, time.endTime)
            val cellularList = signalRawList.filter { it.signal == Signal.CELLULAR.ordinal }
            val wifiList = signalRawList.filter { it.signal == Signal.WIFI.ordinal }


            if (cellularList.isNotEmpty()) {
                val lastCellularEntity = cellularList.last()
                cellularPercentage = lastCellularEntity.strengthPercentage



            previousCellularEntity = cellularList.first()
            var previousWifiEntity = wifiList.first()
            cellularList.forEach { cellularEntity ->
                cellularBandList = getMostUsed(
                    cellularBandList,
                    cellularEntity.band!!,
                    previousCellularEntity.timeStamp,
                    cellularEntity.timeStamp
                )
                cellularNameList = getMostUsed(
                    cellularNameList,
                    cellularEntity.operatorName,
                    previousCellularEntity.timeStamp,
                    cellularEntity.timeStamp
                )
                if (cellularEntity.isRoaming==true) roamingTime += cellularEntity.timeStamp - previousCellularEntity.timeStamp
                previousCellularEntity = cellularEntity
            }

            if (wifiList.isNotEmpty()) {
                var previousWifiEntity = wifiList.first()
                val lastWifiEntity = wifiList.last()

                wifiPercentage = lastWifiEntity.strengthPercentage
                startTime=previousWifiEntity.timeStamp
                endTime=lastWifiEntity.timeStamp
                timeDifference=endTime-startTime
                timeInterval = maxOf(timeDifference / MAX_PLOT_POINTS, MINUTE)

            wifiList.forEach { wifiEntity ->
                wifiLevelList = getMostUsed(
                    wifiLevelList,
                    wifiEntity.level.toString(),
                    previousWifiEntity.timeStamp,
                    wifiEntity.timeStamp
                )
                wifiNameList = getMostUsed(
                    wifiNameList,
                    wifiEntity.operatorName.toString(),
                    previousWifiEntity.timeStamp,
                    wifiEntity.timeStamp
                )
                previousWifiEntity = wifiEntity
                if (previousWifiEntity.timeStamp >= startTime) {
                    timeStamp = formatter.format(previousWifiEntity.timeStamp)
                    signalList.add(SignalEntry(timeStamp, previousWifiEntity.signal, previousWifiEntity.strength))
                    startTime = (timeInterval + previousWifiEntity.timeStamp)
                }
                previousWifiEntity = wifiEntity
            }
                wifiLevelList.sortBy { it.time }
                wifiNameList.sortBy { it.time }

            }

            val cookedDataList = ArrayList<Any>()
            val signalCookedData = SignalCookedData(
                roamingTime.toString(),
                cellularNameList.last().name,
                wifiNameList.last().name,
                cellularBandList.last().name,
                wifiLevelList.last().name,
                wifiPercentage,
                cellularPercentage
            )

            cookedDataList.add(signalCookedData)
            cookedDataList.add(signalList)

            callback.onDone(cookedDataList as ArrayList<T>)
        }
    }

    private fun getRoamingTime(cellularList: List<SignalRaw>): String {
        if (cellularList.isEmpty()) return "0"
        var roamingTime: Long = 0
        var previousSignalEntity = cellularList.first()
        for (i in cellularList) {
            if (i.isRoaming == true) roamingTime += i.timeStamp - previousSignalEntity.timeStamp
            previousSignalEntity = i
        }
        val hours: Int = roamingTime.toInt() / (1000 * 60 * 60)
        val minutes: Int = (roamingTime.toInt() / 1000) % (60 * 60)
        return "$hours hours $minutes min"
    }

    private fun addToList(list: List<SignalRaw>) {
        var startTime: Long
        val endTime: Long
        var timeStamp: String
        if (list.isNotEmpty()) {
            val formatter = SimpleDateFormat("HH:mm dd MMM yyyy", Locale.ENGLISH)
            startTime = list.first().timeStamp
            endTime = list.last().timeStamp
            val timeDifference = endTime - startTime
            val timeInterval = maxOf(timeDifference / MAX_PLOT_POINTS, MINUTE)
            for (signal in list) {
                if (signal.timeStamp >= startTime) {
                    timeStamp = formatter.format(signal.timeStamp)
                    signalList.add(SignalEntry(timeStamp, signal.signal, signal.strength))
                    startTime = (timeInterval + signal.timeStamp)
                }
            }
        }
    }

    private fun getMostUsed(
        usageList: ArrayList<Usage>,
        dataValue: String,
        previousTime: Long,
        currentTime: Long
    ): ArrayList<Usage> {
        if (usageList.none { it.name == dataValue })
            usageList.add(Usage(dataValue, 0))
        usageList.first { it.name == dataValue }.time += (currentTime - previousTime)
        return usageList
    }
}