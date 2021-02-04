package com.example.androidDeviceDetails.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.androidDeviceDetails.fragments.PermittedFragment
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData

class PermittedAppsFragmentAdapter(
    fragmentManager: FragmentManager,
    private var appList: Pair<List<PermittedAppsCookedData>, List<PermittedAppsCookedData>>
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PermittedFragment(appList.second)
            else -> PermittedFragment(appList.first)
        }
    }

    override fun getCount() = 2
}