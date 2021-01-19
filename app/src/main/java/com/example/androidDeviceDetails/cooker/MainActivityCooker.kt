package com.example.androidDeviceDetails.cooker

import android.util.Log
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.MainActivityCookedData
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoRaw
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageRaw
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
                        MainActivityCookedData(
                            null,
                            totalDrop,
                            null,-1
                        )
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
                                null, -1,
                                Pair(totalWifiData, totalCellularData),-1
                            )
                        ) as ArrayList<T>
                    )
                }
                else{
                    callback.onDone(
                        arrayListOf(
                            MainActivityCookedData(
                                null, -1,
                                Pair(1, 1),-1
                            )
                        ) as ArrayList<T>
                    )
                }
            }
        }
        val appInfoCallBack = object : ICookingDone<AppInfoRaw> {
            override fun onDone(outputList: ArrayList<AppInfoRaw>) {
                callback.onDone(
                    arrayListOf(
                        MainActivityCookedData(
                            outputList, -1,
                            null,-1
                        )
                    ) as ArrayList<T>
                )
            }
        }
        val locationDataCallBack=object :ICookingDone<LocationModel>{
            override fun onDone(outputList: ArrayList<LocationModel>) {
                callback.onDone(
                    arrayListOf(
                        MainActivityCookedData(null,-1,null,outputList.size)
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
        LocationCooker().cook(time,locationDataCallBack)
        BatteryCooker().cook(time, batteryCallBack)
        DeviceNetworkUsageCooker().cook(time, deviceNetworkUsageCallBack)
    }
}