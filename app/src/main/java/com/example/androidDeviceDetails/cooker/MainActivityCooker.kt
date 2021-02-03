package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.AppInfoRaw
import com.example.androidDeviceDetails.database.DeviceNetworkUsageRaw
import com.example.androidDeviceDetails.database.LocationModel
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.battery.BatteryAppEntry
import com.example.androidDeviceDetails.models.signal.SignalCookedData

class MainActivityCooker : BaseCooker() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        val total = arrayListOf<Any>()
        val subCallback = object : ICookingDone<Any> {
            override fun onComplete(outputList: ArrayList<Any>) {
                total.addAll(outputList)
                iCookingDone.onComplete(total as ArrayList<T>)
            }
        }
        AppTypeCooker().cook(time, subCallback as ICookingDone<AppInfoRaw>)
        SignalCooker().cook(time, subCallback as ICookingDone<SignalCookedData>)
        BatteryCooker().cook(time, subCallback as ICookingDone<BatteryAppEntry>)
        DeviceNetworkUsageCooker().cook(time, subCallback as ICookingDone<DeviceNetworkUsageRaw>)
        LocationCooker().cook(time, subCallback as ICookingDone<LocationModel>)
    }
}