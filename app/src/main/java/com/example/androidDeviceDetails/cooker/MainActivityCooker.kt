package com.example.androidDeviceDetails.cooker

import android.util.Log
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.MainActivityCookedData
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
        val batteryCallBack = object : ICookingDone<BatteryAppEntry> {
            override fun onDone(outputList: ArrayList<BatteryAppEntry>) {
                var totalDrop = 0L
                for (i in outputList) totalDrop += i.drop
                callback.onDone(
                    arrayListOf(
                        MainActivityCookedData(totalDrop = totalDrop)
                    ) as ArrayList<T>
                )
            }
        }
        val deviceNetworkUsageCallBack = object : ICookingDone<DeviceNetworkUsageRaw> {
            override fun onDone(outputList: ArrayList<DeviceNetworkUsageRaw>) {
                Log.d("callback", "onDone: ")
                if (outputList.isNotEmpty()) {
                    val totalWifiData =
                        outputList.first().transferredDataWifi + outputList.first().receivedDataWifi
                    val totalCellularData =
                        outputList.first().transferredDataMobile + outputList.first().transferredDataMobile
                    callback.onDone(
                        arrayListOf(
                            MainActivityCookedData(
                                deviceNetworkUsage = Pair(
                                    totalWifiData,
                                    totalCellularData
                                )
                            )
                        ) as ArrayList<T>
                    )
                } else {
                    callback.onDone(
                        arrayListOf(
                            MainActivityCookedData(deviceNetworkUsage = Pair(1, 1) )
                        ) as ArrayList<T>
                    )
                }
            }
        }
        val appInfoCallBack = object : ICookingDone<AppInfoRaw> {
            override fun onDone(outputList: ArrayList<AppInfoRaw>) {
                callback.onDone(
                    arrayListOf(
                        MainActivityCookedData(appInfo = outputList)
                    ) as ArrayList<T>
                )
            }
        }
        val locationDataCallBack = object : ICookingDone<LocationModel> {
            override fun onDone(outputList: ArrayList<LocationModel>) {
                Log.d("locationcheck", "onDone: ")
                callback.onDone(
                    arrayListOf(
                        MainActivityCookedData(totalPlacesVisited =  outputList.size)
                    ) as ArrayList<T>
                )
            }
        }
        val signalDataCallBack = object : ICookingDone<SignalRaw> {
            override fun onDone(outputList: ArrayList<SignalRaw>) {
                callback.onDone(
                    arrayListOf(
                        MainActivityCookedData(signalStrength =  outputList.first().strength)
                    ) as ArrayList<T>
                )
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            appInfoCallBack.onDone(
                RoomDB.getDatabase()?.appsDao()?.getAll()
                    ?.filter { it.currentVersionCode != 0L } as ArrayList<AppInfoRaw>
            )
        }
        SignalCooker().cook(time, signalDataCallBack)
        LocationCooker().cook(time, locationDataCallBack)
        BatteryCooker().cook(time, batteryCallBack)
        DeviceNetworkUsageCooker().cook(time, deviceNetworkUsageCallBack)
    }
}