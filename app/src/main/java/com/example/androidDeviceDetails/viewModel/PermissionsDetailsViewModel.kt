package com.example.androidDeviceDetails.viewModel

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.adapters.PermittedAppsFragmentAdapter
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.google.android.material.tabs.TabLayout

class PermissionsDetailsViewModel(
    private val binding: ActivityPermittedAppsBinding,
    val context: Context
) {

    fun onDone(outputList: Array<PermittedAppsCookedData>) {
        var appList = outputList.toList()
        appList = appList.sortedBy { it.apkTitle }
        binding.root.post {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Allowed"))
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Denied"))
            binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = PermittedAppsFragmentAdapter(
                (context as AppCompatActivity).supportFragmentManager,
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

    private fun divideList(appList: List<PermittedAppsCookedData>): Pair<List<PermittedAppsCookedData>, List<PermittedAppsCookedData>> {
        val convertedList = appList.map {
            PermittedAppsCookedData(
                it.packageName, it.apkTitle, it.versionName,
                it.isAllowed
            )
        }
        return convertedList.partition { !it.isAllowed }
    }
}