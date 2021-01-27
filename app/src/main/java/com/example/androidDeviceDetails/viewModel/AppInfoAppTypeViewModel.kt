package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.util.Log
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityAppInfoAppTypeBinding
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfo.EventType
import com.example.androidDeviceDetails.models.database.AppInfoRaw

/**
 * Implements [BaseViewModel]
 */
class AppInfoAppTypeViewModel(
    private val binding: ActivityAppInfoAppTypeBinding,
    val context: Context
) : BaseViewModel() {

    companion object {
        private const val  FILTER_ALL = 0
        private const val  FILTER_SYSTEM = 1
        var eventFilter = FILTER_ALL
        var savedAppList = arrayListOf<AppInfoRaw>()
        var userApps = arrayListOf<AppInfoCookedData>()
        var systemApps = arrayListOf<AppInfoCookedData>()
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

        if (eventFilter != FILTER_ALL) {
            if (eventFilter == FILTER_SYSTEM)
                filteredList.removeAll { !it.isSystemApp }
            else
                filteredList.removeAll { it.isSystemApp }
        }
        filteredList = filteredList.sortedBy { it.appTitle }.toMutableList()
        divideList(filteredList)
        Log.d("AppType", "onDone: ${systemApps.size}")
    }


    private fun divideList(appList: MutableList<AppInfoRaw>){
        if(systemApps.isEmpty() || userApps.isEmpty()) {
            appList.forEach {
                if (it.isSystemApp) {
                    systemApps.add(
                        AppInfoCookedData(
                            it.appTitle,
                            EventType.ALL_EVENTS,
                            it.currentVersionCode,
                            it.uid,
                            it.isSystemApp,
                            it.packageName,
                            it.appSize
                        )
                    )
                } else {
                    userApps.add(
                        AppInfoCookedData(
                            it.appTitle,
                            EventType.ALL_EVENTS,
                            it.currentVersionCode,
                            it.uid,
                            it.isSystemApp,
                            it.packageName,
                            it.appSize
                        )
                    )
                    Log.d("Final", "divideList: ${systemApps.size}")
                }
            }
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
}