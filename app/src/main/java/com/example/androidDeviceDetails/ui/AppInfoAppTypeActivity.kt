package com.example.androidDeviceDetails.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.AppTypeAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityAppInfoAppTypeBinding
import com.example.androidDeviceDetails.models.database.AppInfoRaw
import com.example.androidDeviceDetails.utils.Utils
import com.example.androidDeviceDetails.viewModel.AppInfoAppTypeViewModel
import com.google.android.material.tabs.TabLayout


class AppInfoAppTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppInfoAppTypeBinding
    private lateinit var controller: ActivityController<AppInfoRaw>


    companion object {
        const val NAME = "appType"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_info_app_type)
        controller = ActivityController(
            NAME,
            binding,
            this,
            null,
            supportFragmentManager
        )
        //binding.appTypeListView.isEnabled = true

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("User"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("System"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = AppTypeAdapter(
            supportFragmentManager,
            binding.tabLayout.tabCount,
            controller.viewModel as AppInfoAppTypeViewModel
        )
        binding.viewPager.adapter = adapter

        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

    fun deleteApp(view: View) {
        Utils.uninstallApp(view.tag as String, packageManager, this)
    }


}