package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalEntry
import com.example.androidDeviceDetails.models.TimeInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WifiCooker : BaseCooker() {
    private var db: RoomDB = RoomDB.getDatabase()!!

    override fun <T> cook(time: TimeInterval, callback: ICookingDone<T>) {
        val wifiEntryList = arrayListOf<SignalEntry>()
        GlobalScope.launch {
            val wifiList = db.wifiDao().getAllBetween(time.startTime, time.endTime)
            if (wifiList.isNotEmpty()) {
                for (wifi in wifiList) {
                    wifiEntryList.add(
                        SignalEntry(
                            wifi.timeStamp,
                            wifi.strength,
                            wifi.linkSpeed.toString(),
                            getWifiLevel(wifi.strength)
                        )
                    )
                }
                callback.onDone(wifiEntryList as ArrayList<T>)
            } else callback.onDone(arrayListOf())
        }
    }

    private fun getWifiLevel(strength: Int?): Int {
        if (strength != null) {
            return when {
                strength > -50 -> 3
                strength > -60 -> 2
                strength > -70 -> 1
                else -> 0
            }
        }
        return 0
    }
}