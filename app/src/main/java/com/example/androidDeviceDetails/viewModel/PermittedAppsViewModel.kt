package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.widget.Toast
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermittedAppsListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData

/**
 * Implements [BaseViewModel]
 */
class PermittedAppsViewModel(private val binding: ActivityPermittedAppsBinding, val context: Context, var type:String) :
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
        filteredList = filteredList.sortedBy { it.apk_title }.toMutableList()
        filteredList.removeAll { it.package_name == DeviceDetailsApplication.instance.packageName }
        val allowedList: MutableList<PermittedAppsCookedData> = ArrayList()
        val deniedList: MutableList<PermittedAppsCookedData> = ArrayList()
        for(i in filteredList){
            if(i.isAllowed)
                allowedList.add(i)
            else
                deniedList.add(i)
        }
        binding.root.post {
            binding.permittedAppsListView.adapter =
                PermittedAppsListAdapter(context, R.layout.permitted_app_info_tile, allowedList)
            binding.deniedAppsListView.adapter =
                PermittedAppsListAdapter(context, R.layout.permitted_app_info_tile, deniedList)
        }
    }

    override fun filter(type: Int) {
        eventFilter = type
        onDone(savedAppList)
    }

    private fun onNoData() =
        binding.root.post {
            Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show()
        }
}