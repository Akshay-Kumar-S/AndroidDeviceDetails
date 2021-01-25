package com.example.androidDeviceDetails.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.androidDeviceDetails.fragments.AppTypeUser
import com.example.androidDeviceDetails.viewModel.AppInfoAppTypeViewModel


class AppTypeAdapter(
    fm: FragmentManager,
    private var totalTabs: Int,
    private var appInfoAppTypeViewModel: AppInfoAppTypeViewModel
) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AppTypeUser(AppInfoAppTypeViewModel.userApps)
            else -> AppTypeUser(AppInfoAppTypeViewModel.systemApps)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}