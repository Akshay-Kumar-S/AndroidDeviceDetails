package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.widget.Toast
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermittedAppsListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.models.appInfo.EventType

/**
 * Implements [BaseViewModel]
 */
class PermittedAppsViewModel(private val binding: ActivityPermittedAppsBinding, val context: Context) :
    BaseViewModel() {
    companion object {
        var eventFilter = 4
        var savedAppList = arrayListOf<PermittedAppsCookedData>()
    }

    /**
     * Displays provided data on UI as List view and a donut chart
     *
     * Overrides : [onDone] in [BaseViewModel]
     * @param [outputList] list of cooked data
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> onDone(outputList: ArrayList<T>) {
        val appList = outputList as ArrayList<PermittedAppsCookedData>
        var filteredList = appList.toMutableList()
        savedAppList = appList

//        if (eventFilter != EventType.ALL_EVENTS.ordinal) {
//            filteredList.removeAll { it.eventType != eventFilter }
//        }
        filteredList = filteredList.sortedBy { it.appName }.toMutableList()
        if (appList.isNotEmpty()) filteredList.add(0, appList[0])
        filteredList.removeAll { it.packageName == DeviceDetailsApplication.instance.packageName }
        binding.root.post {
            binding.permittedAppsListView.adapter =
                PermittedAppsListAdapter(context, R.layout.appinfo_tile, filteredList, appList)
        }
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

    private fun onNoData() =
        binding.root.post {
            Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show()
        }
}