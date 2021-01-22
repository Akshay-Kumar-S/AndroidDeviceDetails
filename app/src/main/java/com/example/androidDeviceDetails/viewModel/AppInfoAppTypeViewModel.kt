package com.example.androidDeviceDetails.viewModel

import android.content.Context
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.AppInfoListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityAppInfoAppTypeBinding
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfo.DonutChartData
import com.example.androidDeviceDetails.models.appInfo.EventType
import com.example.androidDeviceDetails.models.appInfo.appType.FilterType
import com.example.androidDeviceDetails.models.database.AppInfoRaw

/**
 * Implements [BaseViewModel]
 */
class AppInfoAppTypeViewModel(
    private val binding: ActivityAppInfoAppTypeBinding,
    val context: Context
) : BaseViewModel() {

    companion object {
        var eventFilter = 0
        var savedAppList = arrayListOf<AppInfoRaw>()
    }

    /**
     * Displays provided data on UI as List view and a donut chart
     *
     * Overrides : [onDone] in [BaseViewModel]
     * @param [outputList] list of cooked data
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> onDone(outputList: ArrayList<T>) {
        val appList = outputList as ArrayList<AppInfoRaw>
        var filteredList = appList.toMutableList()
        savedAppList = appList

        if (eventFilter != FilterType.ALL.ordinal) {
            if (eventFilter == FilterType.SYSTEM.ordinal)
                filteredList.removeAll { !it.isSystemApp }
            else
                filteredList.removeAll { it.isSystemApp }
        }
        filteredList = filteredList.sortedBy { it.appTitle }.toMutableList()
        if (appList.isNotEmpty()) filteredList.add(0, appList[0])
        binding.root.post {
            binding.appTypeListView.adapter =
                AppInfoListAdapter(
                    context,
                    R.layout.appinfo_tile,
                    toCookedData(filteredList),
                    calculateProgressbarStats(),
                    true
                )
        }
    }

    private fun calculateProgressbarStats(): DonutChartData {
        val total = savedAppList.size
        val systemAppsCount = savedAppList.filter { it.isSystemApp }.size
        return DonutChartData(total,total - systemAppsCount, systemAppsCount)
    }

    private fun toCookedData(appList: MutableList<AppInfoRaw>): ArrayList<AppInfoCookedData> {
        val cookedData = arrayListOf<AppInfoCookedData>()
        appList.forEach {
            cookedData.add(
                AppInfoCookedData(
                    it.appTitle,
                    EventType.ALL_EVENTS,
                    it.currentVersionCode,
                    it.uid,
                    it.isSystemApp,
                    it.packageName
                )
            )
        }
        return cookedData
    }

    /**
     * Filters [savedAppList] based on given filter type
     *
     * Overrides : [onDone] in [BaseViewModel]
     * @param [type] Type of filter
     */
    override fun filter(type: Int) {
        eventFilter = type
        onDone(savedAppList)
    }
}