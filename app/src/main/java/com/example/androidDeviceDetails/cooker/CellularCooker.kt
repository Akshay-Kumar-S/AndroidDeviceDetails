package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalEntry
import com.example.androidDeviceDetails.models.TimeInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CellularCooker : BaseCooker() {
    private var db: RoomDB = RoomDB.getDatabase()!!
    override fun <T> cook(time: TimeInterval, callback: ICookingDone<T>) {
        val cellularEntryList = arrayListOf<SignalEntry>()

        GlobalScope.launch {
            val cellularList = db.cellularDao().getAllBetween(time.startTime, time.endTime)
            if (cellularList.isNotEmpty()) {
                for (signal in cellularList) {
                    cellularEntryList.add(
                        SignalEntry(
                            signal.timeStamp,
                            signal.strength,
                            signal.type,
                            signal.level
                        )
                    )
                }
                callback.onDone(cellularEntryList as ArrayList<T>)
            } else callback.onDone(arrayListOf())
        }
    }
}