package com.example.androidDeviceDetails.cooker

import android.util.Log
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.signalModels.SignalCookedData
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
            Log.e("time11", "${System.currentTimeMillis()}")
            val cellularList =
                db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.CELLULAR.ordinal)
            val wifiList =
                db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.WIFI.ordinal)

            val cellularBandUsage = ArrayList<Usage>()
            val cellularOperatorUsage = ArrayList<Usage>()
            val wifiOperatorUsage = ArrayList<Usage>()
            val wifiLevelUsage = ArrayList<Usage>()
            var roamingTime: Long = 0


            var previousSignalEntity = cellularList.first()

            cellularList.forEach { signalEntity ->
                if (cellularBandUsage.none { it.name == signalEntity.band })
                    cellularBandUsage.add(Usage(signalEntity.band, 0))
                cellularBandUsage.first { it.name == previousSignalEntity.band }.time += (signalEntity.timeStamp - previousSignalEntity.timeStamp)

                if (cellularOperatorUsage.none { it.name == signalEntity.operatorName })
                    cellularOperatorUsage.add(Usage(signalEntity.operatorName, 0))
                cellularOperatorUsage.first { it.name == previousSignalEntity.operatorName }.time += (signalEntity.timeStamp - previousSignalEntity.timeStamp)

                if (previousSignalEntity.isRoaming == true) roamingTime += (signalEntity.timeStamp - previousSignalEntity.timeStamp)

                previousSignalEntity = signalEntity
            }
            if(wifiList.isNotEmpty())
            {
                previousSignalEntity = wifiList.first()
                wifiList.forEach { signalEntity ->
                    if (wifiLevelUsage.none { it.name == signalEntity.level.toString() })
                        wifiLevelUsage.add(Usage(signalEntity.level.toString(), 0))
                    wifiLevelUsage.first { it.name == previousSignalEntity.level.toString() }.time += (signalEntity.timeStamp - previousSignalEntity.timeStamp)

                    if (wifiOperatorUsage.none { it.name == signalEntity.operatorName })
                        wifiOperatorUsage.add(Usage(signalEntity.operatorName, 0))
                    wifiOperatorUsage.first { it.name == previousSignalEntity.operatorName }.time += (signalEntity.timeStamp - previousSignalEntity.timeStamp)

                    previousSignalEntity = signalEntity
                }
            }


            cellularBandUsage.sortBy { it.time }
            cellularOperatorUsage.sortBy { it.time }
            wifiLevelUsage.sortBy { it.time }
            wifiOperatorUsage.sortBy { it.time }
            val cookedDataList = ArrayList<SignalCookedData>()
            var cookedData = SignalCookedData(
                roamingTime,
                cellularOperatorUsage.last().name,
                wifiOperatorUsage.last().name,
                cellularBandUsage.last().name!!,
                wifiLevelUsage.last().name!!
            )
            cookedDataList.add(cookedData)
            Log.e(
                "usage",
                "$cellularBandUsage $cellularOperatorUsage $wifiLevelUsage $wifiOperatorUsage $roamingTime"
            )
            if (cellularList.isNotEmpty()) {
                callback.onDone(cookedDataList as ArrayList<T>)
            } else callback.onDone(arrayListOf())
        }
    }
}

