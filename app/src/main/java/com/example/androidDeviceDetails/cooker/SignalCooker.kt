package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimeInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignalCooker : BaseCooker() {
    private var db: RoomDB = RoomDB.getDatabase()!!

    override fun <T> cook(time: TimeInterval, callback: ICookingDone<T>) {
        GlobalScope.launch {
            val signalList = db.signalDao().getAllBetween(time.startTime, time.endTime)
            if (signalList.isNotEmpty()) {
                callback.onDone(signalList as ArrayList<T>)
            } else callback.onDone(arrayListOf())
        }
    }
}