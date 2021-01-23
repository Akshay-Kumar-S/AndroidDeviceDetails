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
import com.example.androidDeviceDetails.utils.SignalList
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
            val cellularList =
                db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.CELLULAR.ordinal)
            val wifiList =
                db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.WIFI.ordinal)

            //TODO handle empty list
            val cookedDataList = ArrayList<Any>()
            val cookedData = SignalCookedData(
                roamingTime(cellularList),
                getMostUsed(cellularList, SignalList.OPERATOR.ordinal),
                getMostUsed(wifiList, SignalList.OPERATOR.ordinal),
                getMostUsed(cellularList, SignalList.BAND.ordinal),
                getMostUsed(wifiList, SignalList.BAND.ordinal),
                wifiList.last().strengthPercentage,
                cellularList.last().strengthPercentage
            )
            cookedDataList.add(cookedData)

            val timeInterval = findTimeInterval(time)
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
                SignalList.BAND.ordinal -> signalEntity.band.toString()
                SignalList.OPERATOR.ordinal -> signalEntity.operatorName
                SignalList.LEVEL.ordinal -> signalEntity.level.toString()
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

    //TODO look for inbuilt functions,add equation
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

 /*   timeDifference <= Time.HOUR -> Time.TWO_MIN
    timeDifference <= Time.SIX_HOUR -> Time.TEN_MIN
    timeDifference <= Time.MIDDAY -> Time.TWENTY_MIN
    timeDifference <= Time.DAY -> Time.THIRTY_MIN
    timeDifference <= Time.THREE_DAY -> Time.TWO_HOUR
    timeDifference <= Time.SIX_DAY -> Time.SIX_HOUR
    timeDifference <= Time.TEN_DAY -> Time.MIDDAY
    else -> Time.DAY*/

    private fun findPattern(time: TimePeriod): String {
        val timeDifference = time.endTime - time.startTime
        return when {
            timeDifference <= Time.TEN_DAY -> "HH:mm dd MMM yyyy"
            else -> "dd MMM yyyy"
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun addToList(list: List<SignalRaw>, timeInterval: Long, pattern: String) {
        var currentTime: Long
        var timeStamp: String
        val formatter = SimpleDateFormat(pattern)
        currentTime = list.first().timeStamp
        for (signal in list) {
            if (signal.timeStamp >= currentTime) {
                timeStamp = formatter.format(signal.timeStamp)
                signalList.add(SignalEntry(timeStamp, signal.signal, signal.strength))
                currentTime = (timeInterval + signal.timeStamp)
            }
        }
    }
}