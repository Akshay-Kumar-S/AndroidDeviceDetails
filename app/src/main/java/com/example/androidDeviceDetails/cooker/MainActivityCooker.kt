package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.MainActivityCookedData
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.appInfoModels.AppsEntity
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivityCooker : BaseCooker() {

    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        val batteryCallBack = object : ICookingDone<BatteryAppEntry> {
            override fun onDone(outputList: ArrayList<BatteryAppEntry>) {
                var totalDrop = 0L
                for (i in outputList) totalDrop += i.drop
                @Suppress("UNCHECKED_CAST")
                callback.onDone(
                    arrayListOf(
                        MainActivityCookedData(
                            null,
                            totalDrop,
                            null
                        )
                    ) as ArrayList<T>
                )
            }
        }
        val deviceNetworkUsageCallBack = object : ICookingDone<DeviceNetworkUsageEntity> {
            override fun onDone(outputList: ArrayList<DeviceNetworkUsageEntity>) {
                val totalWifiData =
                    outputList.first().transferredDataWifi + outputList.first().receivedDataWifi
                val totalCellularData =
                    outputList.first().transferredDataMobile + outputList.first().transferredDataMobile
                @Suppress("UNCHECKED_CAST")
                callback.onDone(
                    arrayListOf(
                        MainActivityCookedData(
                            null, -1,
                            Pair(totalWifiData, totalCellularData)
                        )
                    ) as ArrayList<T>
                )
            }
        }
        val appInfoCallBack = object : ICookingDone<AppsEntity> {
            override fun onDone(outputList: ArrayList<AppsEntity>) {
                @Suppress("UNCHECKED_CAST")
                callback.onDone(
                    arrayListOf(
                        MainActivityCookedData(
                            outputList, -1,
                            null
                        )
                    ) as ArrayList<T>
                )
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            appInfoCallBack.onDone(
                RoomDB.getDatabase()?.appsDao()?.getAll() as ArrayList<AppsEntity>
            )
        }
        BatteryCooker().cook(time, batteryCallBack)
        DeviceNetworkUsageCooker().cook(time, deviceNetworkUsageCallBack)
    }
}