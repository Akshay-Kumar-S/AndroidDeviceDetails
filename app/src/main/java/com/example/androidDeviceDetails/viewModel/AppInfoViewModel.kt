package com.example.androidDeviceDetails.viewModel

import android.content.Context
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.AppInfoListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.managers.AppInfoManager
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfoModels.EventType
import kotlin.math.ceil

class AppInfoViewModel(private val binding: ActivityAppInfoBinding, val context: Context) :
    BaseViewModel() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> onData(outputList: ArrayList<T>) {
        val appList = outputList as ArrayList<AppInfoCookedData>
        if(appList.isEmpty()){
            binding.root.post {
                binding.appInfoListView.adapter = null
                binding.statisticsContainer.isVisible = false
                binding.appInfoListView.isVisible = false
            }
        }
        else {
            var filteredList = appList.toMutableList()
            val eventFilter = binding.statisticsContainer.tag.toString().toIntOrNull()
            if (eventFilter != EventType.ALL_EVENTS.ordinal) {
                filteredList.removeAll { it.eventType.ordinal != eventFilter }
            }
            filteredList = filteredList.sortedBy { it.appName }.toMutableList()
            filteredList.removeAll { it.packageName == DeviceDetailsApplication.instance.packageName }
            if (filteredList.isNotEmpty()) {
                binding.root.post {
                    binding.appInfoListView.adapter = null
                    binding.statisticsContainer.isVisible = true
                    binding.appInfoListView.isVisible = true
                    binding.appInfoListView.adapter =
                        AppInfoListAdapter(
                            context,
                            R.layout.appinfo_tile,
                            filteredList
                        )
                    AppInfoManager.justifyListViewHeightBasedOnChildren(
                        binding.appInfoListView,
                        filteredList.size
                    )
                }
            } else {
                binding.root.post {
                    binding.appInfoListView.isVisible = false
                }
            }

            val total = appList.size.toDouble()
            val enrolledAppCount =
                appList.groupingBy { it.eventType.ordinal == EventType.APP_ENROLL.ordinal }
                    .eachCount()
            val enrolled = ((enrolledAppCount[true] ?: 0).toDouble().div(total).times(100))

            val installedAppCount =
                appList.groupingBy { it.eventType.ordinal == EventType.APP_INSTALLED.ordinal }
                    .eachCount()
            val installed = ceil(((installedAppCount[true] ?: 0).toDouble().div(total).times(100)))

            val updateAppCount =
                appList.groupingBy { it.eventType.ordinal == EventType.APP_UPDATED.ordinal }
                    .eachCount()
            val updated = ceil(((updateAppCount[true] ?: 0).toDouble().div(total).times(100)))

            val uninstalledAppCount =
                appList.groupingBy { it.eventType.ordinal == EventType.APP_UNINSTALLED.ordinal }
                    .eachCount()
            val uninstalled =
                ceil(((uninstalledAppCount[true] ?: 0).toDouble().div(total).times(100)))

            binding.updatedProgressBar.progress = (updated.toInt())
            binding.installedProgressBar.progress = (updated + installed).toInt()
            binding.enrollProgressbar.progress = (updated + installed + enrolled).toInt()
            binding.uninstalledProgressbar.progress =
                (updated + installed + enrolled + uninstalled).toInt()
            binding.pieChartConstraintLayout.post {
                binding.statisticsContainer.isVisible = true
                binding.statsMap.isVisible = true
                binding.enrollCount.text = (enrolledAppCount[true] ?: 0).toString()
                binding.installCount.text = (installedAppCount[true] ?: 0).toString()
                binding.updateCount.text = (updateAppCount[true] ?: 0).toString()
                binding.uninstallCount.text = (uninstalledAppCount[true] ?: 0).toString()
            }
        }
    }
}