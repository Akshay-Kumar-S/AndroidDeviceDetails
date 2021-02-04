package com.example.androidDeviceDetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermittedAppsFragmentAdapter
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson

class PermissionDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermittedAppsBinding

    companion object {
        const val NAME = "permittedApps"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permitted_apps)
        this.title = intent.getStringExtra("permission").toString()
        val appList = Gson().fromJson(
            intent.getStringExtra("appList").toString(),
            Array<PermittedAppsCookedData>::class.java
        )
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.labelBackGround),
            ContextCompat.getColor(this, R.color.white)
        )
        onDone(appList)
    }

    private fun onDone(outputList: Array<PermittedAppsCookedData>) {
        var appList = outputList.toMutableList()
        appList = appList.sortedBy { it.packageName }.toMutableList()
        binding.root.post {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Allowed"))
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Denied"))
            binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = PermittedAppsFragmentAdapter(
                (this as AppCompatActivity).supportFragmentManager,
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

    private fun divideList(appList: MutableList<PermittedAppsCookedData>): Pair<List<PermittedAppsCookedData>, List<PermittedAppsCookedData>> {
        val convertedList = appList.map {
            PermittedAppsCookedData(
                it.packageName, it.apkTitle, it.versionName,
                it.isAllowed
            )
        }
        return convertedList.partition { !it.isAllowed }
    }
}