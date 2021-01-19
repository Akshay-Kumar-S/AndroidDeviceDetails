package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.widget.Toast
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermissionsListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityPermissionsBinding

/**
 * Implements [BaseViewModel]
 */
class PermissionsViewModel(private val binding: ActivityPermissionsBinding, val context: Context) :
    BaseViewModel() {
    companion object {
        var eventFilter = 4
        var savedPermissionList = arrayListOf<String>()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> onDone(outputList: ArrayList<T>) {
        var permissionList = outputList as ArrayList<String>
        var filteredList = permissionList.toMutableList()
        savedPermissionList = permissionList
        if (permissionList.isEmpty()){
            onNoData()
        }
        binding.root.post {
            binding.permissionsListView.adapter =
                PermissionsListAdapter(context, R.layout.permissions_tile, filteredList, permissionList)
        }
    }

    override fun filter(type: Int) {
        eventFilter = type
        onDone(savedPermissionList)
    }

    private fun onNoData() =
        binding.root.post {
            Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show()
        }
}