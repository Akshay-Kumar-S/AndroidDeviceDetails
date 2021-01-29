package com.example.androidDeviceDetails.viewModel

import android.content.Context
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.AppInfoListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfo.DonutChartData
import com.example.androidDeviceDetails.models.appInfo.EventType

/**
 * Implements [BaseViewModel]
 */
class AppInfoViewModel(private val binding: ActivityAppInfoBinding, val context: Context) :
    BaseViewModel() {
    companion object {
        var eventFilter = 4
        var savedAppList = arrayListOf<AppInfoCookedData>()
    }

    /**
     * Displays provided data on UI as List view and a donut chart
     *
     * Overrides : [onComplete] in [BaseViewModel]
     * @param [outputList] list of cooked data
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> onComplete(outputList: ArrayList<T>) {
        val appList = outputList as ArrayList<AppInfoCookedData>
        var filteredList = appList.toMutableList()
        savedAppList = appList

        if (eventFilter != EventType.ALL_EVENTS.ordinal) {
            filteredList.removeAll { it.eventType.ordinal != eventFilter }
        }
        filteredList = filteredList.sortedBy { it.appName }.toMutableList()
        if (appList.isNotEmpty()) filteredList.add(0, appList[0])
        filteredList.removeAll { it.packageName == DeviceDetailsApplication.instance.packageName }
        binding.root.post {
            binding.appInfoListView.adapter =
                AppInfoListAdapter(context, R.layout.appinfo_tile, filteredList, appList,false)
        }
    }

    private fun calculateProgressbarStats() : DonutChartData {
        val total = savedAppList.size
        val enrolledAppCount =
            savedAppList.groupingBy { it.eventType.ordinal == EventType.APP_ENROLL.ordinal }
                .eachCount()[true] ?: 0
        val installedAppCount =
            savedAppList.groupingBy { it.eventType.ordinal == EventType.APP_INSTALLED.ordinal }
                .eachCount()[true] ?: 0
        val updateAppCount =
            savedAppList.groupingBy { it.eventType.ordinal == EventType.APP_UPDATED.ordinal }
                .eachCount()[true] ?: 0
        val uninstalledAppCount =
            savedAppList.groupingBy { it.eventType.ordinal == EventType.APP_UNINSTALLED.ordinal }
                .eachCount()[true] ?: 0
        return DonutChartData(total,enrolledAppCount,installedAppCount,updateAppCount,uninstalledAppCount)
    }

    /**
     * Filters [savedAppList] based on given filter type
     *
     * Overrides : [onComplete] in [BaseViewModel]
     * @param [type] Type of filter
     */
    override fun filter(type: Int) {
        eventFilter = type
        onComplete(savedAppList)
    }
}
