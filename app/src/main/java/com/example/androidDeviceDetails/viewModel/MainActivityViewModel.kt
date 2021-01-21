package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.util.Log
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityMainBinding
import com.example.androidDeviceDetails.models.MainActivityCookedData
import com.example.androidDeviceDetails.utils.Utils
import java.util.*
import kotlin.math.ceil
import kotlin.math.pow


class MainActivityViewModel(private val binding: ActivityMainBinding, val context: Context) :
    BaseViewModel() {
    private var mainActivityModel = MainActivityCookedData(null, -1, null, -1, Int.MIN_VALUE)
    override fun <T> onDone(outputList: ArrayList<T>) {
        val finalList = outputList.filterIsInstance<MainActivityCookedData>()
        if (outputList.isNotEmpty()) {
            val firstElement = finalList.first()
            when {
                firstElement.appInfo != null -> mainActivityModel.appInfo = firstElement.appInfo
                firstElement.totalDrop != -1L -> mainActivityModel.totalDrop =
                    firstElement.totalDrop
                firstElement.deviceNetworkUsage != null -> mainActivityModel.deviceNetworkUsage =
                    firstElement.deviceNetworkUsage
                firstElement.totalPlacesVisited != -1 -> mainActivityModel.totalPlacesVisited =
                    firstElement.totalPlacesVisited
                firstElement.signalStrength != -1 -> mainActivityModel.signalStrength =
                    firstElement.signalStrength
            }
            binding.root.post { updateUI() }
        }
    }

    private fun refresh() {
        mainActivityModel = MainActivityCookedData()
    }

    private fun updateUI() {

        if (mainActivityModel.appInfo != null) {
            val appInfo = mainActivityModel.appInfo
            val systemAppsCount = appInfo!!.filter { it.isSystemApp }.size
            val systemAppsProgress = ceil(
                ((systemAppsCount).toDouble().div(appInfo.size).times(100))
            ).toInt()
            val userAppsCount = mainActivityModel.appInfo!!.size - systemAppsCount
            val userAppsProgress = ceil(
                ((userAppsCount).toDouble().div(appInfo.size).times(100))
            ).toInt()
            binding.appInfo.label1Value.text = systemAppsCount.toString()
            binding.appInfo.label2Value.text = userAppsCount.toString()
            binding.appInfo.progressbarFirst.progress = systemAppsProgress + userAppsProgress
            binding.appInfo.progressbarSecond.progress = userAppsProgress
            Log.d("MainViewModel", "AppInfo  ")
            mainActivityModel = MainActivityCookedData()
        } else if (mainActivityModel.totalDrop != -1L) {
            binding.batteryInfo.mainValue.text = mainActivityModel.totalDrop.toInt().toString()
            mainActivityModel = MainActivityCookedData()
            Log.d("MainViewModel", "Battery data  ")
        } else if (mainActivityModel.deviceNetworkUsage != null) {
            val wifiData = mainActivityModel.deviceNetworkUsage!!.first / 1024.0.pow(2.toDouble())
            val cellularData =
                mainActivityModel.deviceNetworkUsage!!.second / 1024.0.pow(2.toDouble())
            val total = wifiData + cellularData
            val wifiDataProgress = ceil(
                ((wifiData).div(total).times(100))
            ).toInt()
            val cellularDataProgress = ceil(
                ((cellularData).div(total).times(100))
            ).toInt()
            binding.networkUsage.label1Value.text =
                Utils.getFileSize(mainActivityModel.deviceNetworkUsage!!.first)
            binding.networkUsage.label2Value.text =
                Utils.getFileSize(mainActivityModel.deviceNetworkUsage!!.second)
            binding.networkUsage.progressbarFirst.progress = wifiDataProgress + cellularDataProgress
            binding.networkUsage.progressbarSecond.progress = cellularDataProgress
            Log.d("MainViewModel", "Network data  ")
        } else if (mainActivityModel.totalPlacesVisited != -1) {
            binding.locationInfo.mainValue.text = mainActivityModel.totalPlacesVisited.toString()
            mainActivityModel = MainActivityCookedData()
            Log.d("MainViewModel", "Location data  ")
        } else if (mainActivityModel.signalStrength != Int.MIN_VALUE) {
            binding.signalData.mainValue.text = mainActivityModel.totalPlacesVisited.toString()
            mainActivityModel = MainActivityCookedData()
            Log.d("MainViewModel", "Signal data  ")
        }

    }
}