package com.example.androidDeviceDetails.viewModel
//
//import android.content.Context
//import com.example.androidDeviceDetails.R
//import com.example.androidDeviceDetails.adapters.PermittedAppsAdapter
//import com.example.androidDeviceDetails.base.BaseViewModel
//import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
//import com.example.androidDeviceDetails.DeviceDetailsApplication
//import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppList
//import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
//import com.example.androidDeviceDetails.viewModel.AppInfoViewModel.Companion.savedAppList
//
///**
// * Implements [BaseViewModel]
// */
//class PermittedAppsViewModel(
//    private val binding: ActivityPermittedAppsBinding,
//    val context: Context
//) :
//    BaseViewModel() {
//    companion object {
//        var eventFilter = 1
//        var savedAppList = arrayListOf<PermittedAppsCookedData>()
//    }
//
//    /**
//     * Displays provided data on UI as List view and a donut chart
//     *
//     * Overrides : [onDone] in [BaseViewModel]
//     * @param [outputList] list of cooked data
//     */
//    @Suppress("UNCHECKED_CAST")
//    override fun <T> onDone(outputList: ArrayList<T>) {
//        val appList = outputList as ArrayList<PermittedAppsCookedData>
//        var filteredList = appList.toMutableList()
//        savedAppList = appList
//        filteredList = filteredList.sortedBy { it.apk_title }.toMutableList()
//        filteredList.removeAll { it.package_name == DeviceDetailsApplication.instance.packageName }
//        val savedAppList: MutableList<PermittedAppsCookedData> = ArrayList()
//        if (eventFilter == 1) {
//            savedAppList.clear()
//            for (i in filteredList) {
//                if (i.isAllowed)
//                    savedAppList.add(i)
//            }
//        } else {
//            savedAppList.clear()
//            for (i in filteredList) {
//                if (!i.isAllowed)
//                    savedAppList.add(i)
//            }
//        }
//        binding.root.post {
//            binding.permittedAppsListView.adapter =
//                PermittedAppsAdapter(context, R.layout.permitted_app_info_tile, savedAppList)
//        }
//    }
//
//    /**
//     * Filters [savedAppList] based on given filter type
//     *
//     * Overrides : [onDone] in [BaseViewModel]
//     * @param [type] Type of filter
//     */
//    override fun filter(type: Int) {
//        eventFilter = type
//        onDone(savedAppList)
//    }
//}

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.adapters.PermittedAppsFragmentAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.google.android.material.tabs.TabLayout


/**
 * Implements [BaseViewModel]
 */
class PermittedAppsViewModel(val binding: ActivityPermittedAppsBinding, val context: Context) :
    BaseViewModel() {

    /**
     * Displays provided data on UI as List view
     *
     * Overrides : [onDone] in [BaseViewModel]
     * @param [outputList] list of cooked data
     */
    override fun <T> onDone(outputList: ArrayList<T>) {
        var appList =
            (outputList.filterIsInstance<PermittedAppsCookedData>() as ArrayList).toMutableList()
        appList = appList.sortedBy { it.package_name }.toMutableList()
        binding.root.post {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Allowed"))
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Denied"))
            binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = PermittedAppsFragmentAdapter(
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

    private fun divideList(appList: MutableList<PermittedAppsCookedData>): Pair<List<PermittedAppsCookedData>, List<PermittedAppsCookedData>> {
        val convertedList = appList.map {
            PermittedAppsCookedData(
                it.package_name, it.apk_title, it.version_name,
                it.isAllowed
            )
        }
        return convertedList.partition { !it.isAllowed }
    }

}