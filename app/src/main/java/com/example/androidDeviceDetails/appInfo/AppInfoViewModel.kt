package com.example.androidDeviceDetails.appInfo

import android.content.Context
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.appInfo.models.AppInfoCookedData
import com.example.androidDeviceDetails.appInfo.models.EventType
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import kotlin.math.ceil

class AppInfoViewModel(private val binding: ActivityAppInfoBinding) {

    fun updateDonutChart(appList: List<AppInfoCookedData>) {
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

    fun updateAppList(filteredList: MutableList<AppInfoCookedData>, context: Context) {
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
    }

    fun clearDisplay() {
        binding.root.post {
            binding.appInfoListView.adapter = null
            binding.statisticsContainer.isVisible = false
            binding.appInfoListView.isVisible = false
        }
    }
}