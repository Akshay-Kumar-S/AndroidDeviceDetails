package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.MainActivityAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityMainBinding
import com.example.androidDeviceDetails.models.ActivityTag
import com.example.androidDeviceDetails.models.CardItem
import com.example.androidDeviceDetails.models.LayoutType
import com.example.androidDeviceDetails.models.MainActivityCookedData
import java.util.*
import kotlin.math.ceil

class MainActivityViewModel(
    private val binding: ActivityMainBinding,
    val context: Context
) : BaseViewModel() {
    private var mainActivityModel = MainActivityCookedData(null, -1, null)
    private val arrayList = arrayListOf<CardItem>()
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
            }
            binding.root.post { updateUI() }
        }
    }

    val icons = intArrayOf(
        R.drawable.app_info,
        R.drawable.battery,
    )
    val iconsName = arrayOf(
        "App Info",
        "Battery Usage",
        "Location",
        "Network Usage",
        "Signal Data",
    )

    private fun updateUI() {

        val recyclerView = binding.root.findViewById<View>(R.id.recycler_view) as RecyclerView

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.itemAnimator = DefaultItemAnimator()

        val itemModel = CardItem()

        if (mainActivityModel.appInfo != null && arrayList.none { it.tag == ActivityTag.APP_INFO.ordinal }) {
            itemModel.tag = ActivityTag.APP_INFO.ordinal
            itemModel.image = R.drawable.app_info
            itemModel.title = "App Info"
            val appInfo = mainActivityModel.appInfo
            itemModel.layoutType = LayoutType.PROGRESSBAR_LAYOUT.ordinal
            itemModel.label1 = "System Apps : "
            val systemAppsCount = appInfo!!.filter { it.isSystemApp }.size
            itemModel.label1Value = systemAppsCount.toString()
            val systemAppsProgress = ceil(
                ((systemAppsCount).toDouble().div(appInfo.size).times(100))
            ).toInt()
            itemModel.label2 = "User Apps : "
            val userAppsCount = mainActivityModel.appInfo!!.size - systemAppsCount
            itemModel.label2Value = userAppsCount.toString()
            val userAppsProgress = ceil(
                ((userAppsCount).toDouble().div(appInfo.size).times(100))
            ).toInt()
            itemModel.progressbarFirst = systemAppsProgress + userAppsProgress
            itemModel.progressbarSecond = userAppsProgress
            arrayList.add(itemModel)
            Log.d("MainViewModel", "AppInfo  ")
        }
        else if (mainActivityModel.totalDrop != -1L && arrayList.none { it.tag == ActivityTag.BATTERY_DATA.ordinal }) {
            itemModel.tag = ActivityTag.BATTERY_DATA.ordinal
            itemModel.image = R.drawable.battery
            itemModel.title = "Battery Usage"
            itemModel.layoutType = LayoutType.SINGLE_VALUE_LAYOUT.ordinal
            itemModel.mainValue = mainActivityModel.totalDrop.toInt()
            itemModel.superscript = "%"
            itemModel.subscript = "Consumed"
            arrayList.add(itemModel)
            Log.d("MainViewModel", "Battery data  ")
        }
        else if (mainActivityModel.deviceNetworkUsage != null && arrayList.none { it.tag == ActivityTag.DEVICE_NETWORK_USAGE.ordinal }) {
            itemModel.tag = ActivityTag.DEVICE_NETWORK_USAGE.ordinal
            itemModel.image = R.drawable.database
            itemModel.title = "Network Usage"
            itemModel.layoutType = LayoutType.PROGRESSBAR_LAYOUT.ordinal
            itemModel.label1 = "Wifi Data : "
            val wifiData = mainActivityModel.deviceNetworkUsage!!.first / 1024
            val cellularData = mainActivityModel.deviceNetworkUsage!!.second / 1024
            val total = wifiData + cellularData
            itemModel.label1Value = "$wifiData MB"
            val cellularDataProgress = ceil(
                ((wifiData).toDouble().div(cellularData).times(100))
            ).toInt()
            itemModel.label2 = "Cellular Data : "
            itemModel.label2Value = "$cellularData MB"
            val wifiDataProgress = ceil(
                ((cellularData).toDouble().div(total).times(100))
            ).toInt()
            itemModel.progressbarFirst = cellularDataProgress + wifiDataProgress
            itemModel.progressbarSecond = wifiDataProgress
            arrayList.add(itemModel)
            Log.d("MainViewModel", "AppInfo  ")
        }
        Log.d("MainViewModel", "onDone: ${arrayList.size}")

        //add in array list


        val adapter = MainActivityAdapter(context, arrayList)
        recyclerView.adapter = adapter
    }
}