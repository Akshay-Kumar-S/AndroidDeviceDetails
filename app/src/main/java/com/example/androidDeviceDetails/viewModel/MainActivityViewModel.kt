package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.util.Log
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityMainBinding
import com.example.androidDeviceDetails.models.battery.BatteryAppEntry
import com.example.androidDeviceDetails.models.database.AppInfoRaw
import com.example.androidDeviceDetails.models.database.DeviceNetworkUsageRaw
import com.example.androidDeviceDetails.models.location.LocationDisplayModel
import com.example.androidDeviceDetails.models.signal.SignalRaw
import com.example.androidDeviceDetails.utils.Utils
import kotlin.math.ceil
import kotlin.math.pow


class MainActivityViewModel(private val binding: ActivityMainBinding, val context: Context) :
    BaseViewModel() {
    override fun <T> onDone(outputList: ArrayList<T>) {
        val appInfoList = arrayListOf<AppInfoRaw>()
        val batteryList = arrayListOf<BatteryAppEntry>()
        val deviceNetworkUsageList = arrayListOf<DeviceNetworkUsageRaw>()
        val locationList = arrayListOf<LocationDisplayModel>()
        val signalList = arrayListOf<SignalRaw>()
        for (i in outputList) {
            when (i) {
                is AppInfoRaw -> appInfoList.add(i as AppInfoRaw)
                is BatteryAppEntry -> batteryList.add(i as BatteryAppEntry)
                is DeviceNetworkUsageRaw -> deviceNetworkUsageList.add(i as DeviceNetworkUsageRaw)
                is LocationDisplayModel -> locationList.add(i as LocationDisplayModel)
                is SignalRaw -> signalList.add(i as SignalRaw)
            }
        }
        binding.root.post {
            if (appInfoList.isNotEmpty()) updateAppInfoCard(appInfoList)
            if (batteryList.isNotEmpty()) updateBatteryCard(batteryList)
            if (deviceNetworkUsageList.isNotEmpty()) updateDeviceNetworkUsageCard(
                deviceNetworkUsageList
            )
            if (locationList.isNotEmpty()) updateLocationDataCard(locationList)
            if (signalList.isNotEmpty()) updateSignalDataCard(signalList)
        }

    }

    private fun updateBatteryCard(outputList: ArrayList<BatteryAppEntry>) {
        val totalDrop = outputList.sumBy { it.drop }
        binding.batteryInfo.mainValue.text = totalDrop.toString()
    }

    private fun updateAppInfoCard(outputList: ArrayList<AppInfoRaw>) {
        val systemAppsCount = outputList.filter { it.isSystemApp }.size
        val systemAppsProgress = ceil(
            ((systemAppsCount).toDouble().div(outputList.size).times(100))
        ).toInt()
        val userAppsCount = outputList.size - systemAppsCount
        val userAppsProgress = ceil(
            ((userAppsCount).toDouble().div(outputList.size).times(100))
        ).toInt()
        binding.appInfo.label1Value.text = systemAppsCount.toString()
        binding.appInfo.label2Value.text = userAppsCount.toString()
        binding.appInfo.progressbarFirst.progress = systemAppsProgress + userAppsProgress
        binding.appInfo.progressbarSecond.progress = userAppsProgress
        Log.d("MainViewModel", "AppInfo  ")
    }

    private fun updateDeviceNetworkUsageCard(outputList: ArrayList<DeviceNetworkUsageRaw>) {
        val wifiData =
            (outputList.first().transferredDataWifi + outputList.first().receivedDataWifi) / 1024.0.pow(
                2.toDouble()
            )
        val cellularData =
            (outputList.first().transferredDataMobile + outputList.first().transferredDataMobile) / 1024.0.pow(
                2.toDouble()
            )
        val total = wifiData + cellularData
        val wifiDataProgress = ceil(
            ((wifiData).div(total).times(100))
        ).toInt()
        val cellularDataProgress = ceil(
            ((cellularData).div(total).times(100))
        ).toInt()
        binding.networkUsage.label1Value.text =
            Utils.getFileSize(outputList.first().transferredDataWifi + outputList.first().receivedDataWifi)
        binding.networkUsage.label2Value.text =
            Utils.getFileSize(outputList.first().transferredDataMobile + outputList.first().transferredDataMobile)
        binding.networkUsage.progressbarFirst.progress = wifiDataProgress + cellularDataProgress
        binding.networkUsage.progressbarSecond.progress = cellularDataProgress
    }

    private fun updateLocationDataCard(outputList: ArrayList<LocationDisplayModel>) {
        binding.locationInfo.mainValue.text = outputList.size.toString()
    }

    private fun updateSignalDataCard(outputList: ArrayList<SignalRaw>) {
        binding.signalData.mainValue.text = outputList.first().strength.toString()
    }
}