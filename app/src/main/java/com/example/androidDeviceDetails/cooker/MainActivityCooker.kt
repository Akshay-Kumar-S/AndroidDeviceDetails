package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.battery.BatteryAppEntry
import com.example.androidDeviceDetails.models.database.AppInfoRaw
import com.example.androidDeviceDetails.models.database.DeviceNetworkUsageRaw
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.location.LocationModel
import com.example.androidDeviceDetails.models.signal.SignalRaw
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivityCooker : BaseCooker() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        val total = arrayListOf<Any>()
        val subCallback = object : ICookingDone<Any> {
            override fun onDone(outputList: ArrayList<Any>) {
                total.addAll(outputList)
                callback.onDone(total as ArrayList<T>)
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            (subCallback as ICookingDone<AppInfoRaw>)
                .onDone(RoomDB.getDatabase()?.appsDao()?.getAll()?.filter {
                    it.currentVersionCode != 0L
                } as ArrayList<AppInfoRaw>
                )
        }
        SignalCooker().cook(time, subCallback as ICookingDone<SignalRaw>)
        BatteryCooker().cook(time, subCallback as ICookingDone<BatteryAppEntry>)
        DeviceNetworkUsageCooker().cook(time, subCallback as ICookingDone<DeviceNetworkUsageRaw>)
        LocationCooker().cook(time, subCallback as ICookingDone<LocationModel>)
    }
}