package com.example.androidDeviceDetails.viewModel

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.adapters.AppTypeAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.database.AppInfoRaw
import com.example.androidDeviceDetails.databinding.ActivityAppTypeBinding
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfo.EventType
import com.google.android.material.tabs.TabLayout


/**
 * Implements [BaseViewModel]
 */
class AppTypeViewModel(val binding: ActivityAppTypeBinding, val context: Context) :
    BaseViewModel() {

    /**
     * Displays provided data on UI as List view
     *
     * Overrides : [onDone] in [BaseViewModel]
     * @param [outputList] list of cooked data
     */
    override fun <T> onComplete(outputList: ArrayList<T>) {
        var appList = (outputList.filterIsInstance<AppInfoRaw>() as ArrayList).toMutableList()
        appList = appList.sortedBy { it.appTitle }.toMutableList()
        binding.root.post {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("User App"))
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("System System"))
            binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = AppTypeAdapter(
                (context as AppCompatActivity).supportFragmentManager, binding.tabLayout.tabCount,
                divideList(appList)
            )
            binding.viewPager.adapter = adapter
            binding.viewPager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    binding.tabLayout
                )
            )
            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    binding.viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }

    private fun divideList(appList: MutableList<AppInfoRaw>): Pair<List<AppInfoCookedData>, List<AppInfoCookedData>> {
        val convertedList = appList.map {
            AppInfoCookedData(
                it.appTitle, EventType.EVENTS, it.currentVersionCode, it.uid,
                it.isSystemApp, it.packageName, it.appSize
            )
        }
        return convertedList.partition { !it.isSystemApp }
    }
}