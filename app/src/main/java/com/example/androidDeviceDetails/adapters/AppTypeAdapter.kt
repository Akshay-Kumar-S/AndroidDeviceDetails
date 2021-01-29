package com.example.androidDeviceDetails.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.androidDeviceDetails.fragments.AppTypeFragment
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData

class AppTypeAdapter(
    fm: FragmentManager, private var totalTabs: Int,
    private var appList: Pair<List<AppInfoCookedData>, List<AppInfoCookedData>>
) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AppTypeFragment(appList.first)
            else -> AppTypeFragment(appList.second)
        }
    }

    override fun getCount() = totalTabs

}