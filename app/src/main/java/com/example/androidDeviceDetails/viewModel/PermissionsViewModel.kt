package com.example.androidDeviceDetails.viewModel

import android.content.Context
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermissionsAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityPermissionsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData

/**
 * Implements [BaseViewModel]
 */
class PermissionsViewModel(private val binding: ActivityPermissionsBinding, val context: Context) :
    BaseViewModel() {

    /**
     * Displays provided data on UI as List view
     *
     * Overrides : [onDone] in [BaseViewModel]
     * @param [outputList] list of cooked data
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> onDone(outputList: ArrayList<T>) {
        var permissionList =
            outputList as ArrayList<Pair<List<String>, List<PermittedAppsCookedData>>>
        binding.root.post {
            binding.permissionsListView.adapter =
                PermissionsAdapter(context, R.layout.permissions_tile, permissionList[0].first)
        }
    }
}