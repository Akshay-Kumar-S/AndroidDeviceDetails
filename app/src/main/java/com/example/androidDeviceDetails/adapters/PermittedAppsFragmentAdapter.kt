package com.example.androidDeviceDetails.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.androidDeviceDetails.fragments.PermittedFragment
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.viewModel.PermissionsDetailsViewModel

class PermittedAppsFragmentAdapter(
    fragmentManager: FragmentManager,
    private var appList: List<PermittedAppsCookedData>
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val allowedAppsList = appList.partition { !it.isAllowed }.first
        val deniedAppsList = appList.partition { !it.isAllowed }.second
        return when (position) {
            PermissionsDetailsViewModel.allowedAppsFragment -> PermittedFragment(allowedAppsList)
            else -> PermittedFragment(deniedAppsList)
        }
    }

    override fun getCount() = 2
}