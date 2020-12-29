package com.example.androidDeviceDetails.controller

import android.content.Context
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.cooker.AppInfoCooker
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.interfaces.IAppInfoCookedData
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfoModels.EventType
import com.example.androidDeviceDetails.viewModel.AppInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppInfoController(binding: ActivityAppInfoBinding, var context: Context) {

    private var viewModel: AppInfoViewModel = AppInfoViewModel(binding,context)
    private var eventFilter: Int = EventType.ALL_EVENTS.ordinal

    private val appInfoData = object : IAppInfoCookedData {
        override fun onDataReceived(appList: List<AppInfoCookedData>) {
            var filteredList = appList.toMutableList()
            if (eventFilter != EventType.ALL_EVENTS.ordinal) {
                filteredList.removeAll { it.eventType.ordinal != eventFilter }
            }
            filteredList = filteredList.sortedBy { it.appName }.toMutableList()
            filteredList.removeAll { it.packageName == DeviceDetailsApplication.instance.packageName }
            viewModel.updateAppList(filteredList, context)
            viewModel.updateDonutChart(appList)
        }

        override fun onNoData() {
            viewModel.clearDisplay()
        }
    }

    fun setAppIfoData(
        startTime: Long,
        endTime: Long,
        eventFilter: Int
    ) {
        this.eventFilter = eventFilter
        GlobalScope.launch(Dispatchers.IO) {
            AppInfoCooker.createInstance()
                .getAppsBetween(startTime, endTime, appInfoData)
        }
    }


}