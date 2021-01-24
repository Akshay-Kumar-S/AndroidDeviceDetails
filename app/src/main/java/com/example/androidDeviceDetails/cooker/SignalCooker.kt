package com.example.androidDeviceDetails.cooker

import android.annotation.SuppressLint
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.database.SignalRaw
import com.example.androidDeviceDetails.models.signalModels.SignalCookedData
import com.example.androidDeviceDetails.models.signalModels.SignalEntry
import com.example.androidDeviceDetails.models.signalModels.Usage
import com.example.androidDeviceDetails.utils.Signal
import com.example.androidDeviceDetails.utils.Time
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

/**
 * Implements [BaseCooker].
 * A cooker class for handling the logic for cooking signal data.
 **/
class SignalCooker : BaseCooker() {
    private var db: RoomDB = RoomDB.getDatabase()!!
    private val signalList = arrayListOf<SignalEntry>()


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
            val cellularList =
                db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.CELLULAR.ordinal)
            val wifiList =
                db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.WIFI.ordinal)
            if (wifiList.isNotEmpty()) wifiPercentage = wifiList.last().strengthPercentage
            if (cellularList.isNotEmpty()) cellularPercentage =
                cellularList.last().strengthPercentage

            val cookedDataList = ArrayList<Any>()
            val cookedData = SignalCookedData(
                roamingTime(cellularList),
                getMostUsed(cellularList, OPERATOR),
                getMostUsed(wifiList, OPERATOR),
                getMostUsed(cellularList, BAND),
                getMostUsed(wifiList, LEVEL),
                wifiPercentage,
                cellularPercentage
            )
            cookedDataList.add(cookedData)

            val timeDifference = time.endTime - time.startTime
            val timeInterval = timeDifference / PLOT_POINTS
            val pattern = findPattern(time)
            addToList(cellularList, timeInterval, pattern)
            addToList(wifiList, timeInterval, pattern)
            cookedDataList.add(signalList)

            if (cookedDataList.isNotEmpty()) {
                callback.onDone(cookedDataList as ArrayList<T>)
            } else callback.onDone(arrayListOf())
        }
    }

    private fun getMostUsed(
        rawList: List<SignalRaw>,
        data: Int,
    ): String {
        if (rawList.isEmpty()) return "no data"
        val usageList = ArrayList<Usage>()
        var dataValue: String
        var previousSignalEntity = rawList.first()
        rawList.forEach { signalEntity ->
            dataValue = when (data) {
                BAND -> signalEntity.band.toString()
                OPERATOR -> signalEntity.operatorName
                LEVEL -> signalEntity.level.toString()
                else -> signalEntity.operatorName
            }
            if (usageList.none { it.name == dataValue })
                usageList.add(Usage(dataValue, 0))
            usageList.first { it.name == dataValue }.time += (signalEntity.timeStamp - previousSignalEntity.timeStamp)
            previousSignalEntity = signalEntity
        }
        usageList.sortBy { it.time }
        return usageList.last().name
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

/*
       private fun findTimeInterval(time: TimePeriod): Long {
           val timeDifference = time.endTime - time.startTime
           return when {
              // timeDifference <= Time.HOUR -> Time.TWO_MIN
               //timeDifference <= Time.SIX_HOUR -> Time.TEN_MIN
               timeDifference <= Time.MIDDAY -> Time.TWO_MIN
              // timeDifference <= Time.DAY -> Time.THIRTY_MIN
               //timeDifference <= Time.THREE_DAY -> Time.TWO_HOUR
               timeDifference <= Time.SIX_DAY -> Time.SIX_HOUR
             //  timeDifference <= Time.TEN_DAY -> Time.MIDDAY
               else -> Time.DAY
           }
       }
   */

    private fun findPattern(time: TimePeriod): String {
        val timeDifference = time.endTime - time.startTime
        return when {
            timeDifference <= Time.TEN_DAY -> "HH:mm dd MMM yyyy"
            else -> "dd MMM yyyy"
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun addToList(list: List<SignalRaw>, timeInterval: Long, pattern: String) {
        var startTime: Long
        var timeStamp: String
        if (list.isNotEmpty()) {
            val formatter = SimpleDateFormat(pattern)
            startTime = list.first().timeStamp
            for (signal in list) {
                if (signal.timeStamp >= startTime) {
                    timeStamp = formatter.format(signal.timeStamp)
                    signalList.add(SignalEntry(timeStamp, signal.signal, signal.strength))
                    startTime = (timeInterval + signal.timeStamp)
                }
            }
        }
    }

    companion object {
        const val PLOT_POINTS: Int = 30
        const val LEVEL = 1
        const val OPERATOR = 2
        const val BAND = 3
    }
}