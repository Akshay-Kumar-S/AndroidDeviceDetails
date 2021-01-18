package com.example.androidDeviceDetails.cooker

import android.util.Log
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
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
     * @param iCookingDone A callback that accepts the cooked list once cooking is done
     */
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        GlobalScope.launch {
            Log.e("time11", "${System.currentTimeMillis()}")
            val signalList = db.signalDao().getAllBetween(time.startTime, time.endTime)
            val bandUsageList = ArrayList<bandUsage>()
            var previousSignalEntity = signalList.first()

            signalList.forEach { signalEntity ->
                if (bandUsageList.none { it.bandName == signalEntity.band })
                    bandUsageList.add(bandUsage(signalEntity.band, 0))

                bandUsageList.first { it.bandName == previousSignalEntity.band }.time+=(signalEntity.timeStamp- previousSignalEntity.timeStamp)
                previousSignalEntity=signalEntity
            }


            bandUsageList.sortBy { it.time }
            var hignestUsedBand = bandUsageList.last()
            Log.e("usage", "$bandUsageList")
            if (signalList.isNotEmpty()) {
                iCookingDone.onComplete(signalList as ArrayList<T>)
            } else iCookingDone.onComplete(arrayListOf())
        }
    }
}

data class bandUsage(
    var bandName: String? = null,
    var time: Long,

    )