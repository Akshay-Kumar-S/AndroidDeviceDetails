package com.example.androidDeviceDetails.cooker

import android.util.Log
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.utils.Signal
import com.example.androidDeviceDetails.models.signalModels.Usage
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
            val cellularList = db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.CELLULAR.ordinal)
            val wifiList = db.signalDao().getAllBetween(time.startTime, time.endTime, Signal.WIFI.ordinal)
            val bandUsageList = ArrayList<Usage>()

            val cellularBandUsage = ArrayList<Usage>()
            var previousSignalEntity = cellularList.first()

            cellularList.forEach { signalEntity ->
                if (bandUsageList.none { it.bandName == signalEntity.band })
                    bandUsageList.add(Usage(signalEntity.band, 0))

                bandUsageList.first { it.bandName == previousSignalEntity.band }.time+=(signalEntity.timeStamp- previousSignalEntity.timeStamp)
                previousSignalEntity=signalEntity
            }


            bandUsageList.sortBy { it.time }
            var hignestUsedBand = bandUsageList.last()
            Log.e("usage", "$bandUsageList")
            if (cellularList.isNotEmpty()) {
                callback.onDone(cellularList as ArrayList<T>)
            } else callback.onDone(arrayListOf())
        }
    }
}

