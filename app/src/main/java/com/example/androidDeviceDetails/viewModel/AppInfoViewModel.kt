package com.example.androidDeviceDetails.viewModel

import android.content.Context
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.AppInfoListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.collectors.AppInfoManager
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfoModels.EventType

/**
 * Implements [BaseViewModel]
 */
class AppInfoViewModel(private val binding: ActivityAppInfoBinding, val context: Context) :
    BaseViewModel() {
    companion object {
        var eventFilter = 0
    }

    /**
     * Displays provided data on UI as List view and a donut chart
     *
     * Overrides : [onDone] in [BaseViewModel]
     * @param [outputList] list of cooked data
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> onDone(outputList: ArrayList<T>) {
        val appList = outputList as ArrayList<AppInfoCookedData>
        AppInfoManager.appList = appList
        if (appList.isEmpty()) {
            binding.root.post {
                binding.appInfoListView.adapter = null
                binding.appInfoListView.isVisible = false
            }
        } else {
            var filteredList = appList.toMutableList()
            if (eventFilter != EventType.ALL_EVENTS.ordinal) {
                filteredList.removeAll { it.eventType.ordinal != eventFilter }
            }
            filteredList = filteredList.sortedBy { it.appName }.toMutableList()
            filteredList.add(0, appList[0])
            filteredList.removeAll { it.packageName == DeviceDetailsApplication.instance.packageName }
            binding.root.post {
                binding.appInfoListView.adapter =
                    AppInfoListAdapter(context, R.layout.appinfo_tile, filteredList, appList)
            }
        }
    }

    /**
     * Filters [AppInfoManager.appList] based on given filter type
     *
     * Overrides : [onDone] in [BaseViewModel]
     * @param [type] Type of filter
     */
    override fun filter(type: Int) {
        eventFilter = type
        onDone(AppInfoManager.appList)
    }
}