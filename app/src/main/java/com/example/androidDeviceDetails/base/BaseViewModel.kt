package com.example.androidDeviceDetails.base

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.example.androidDeviceDetails.activities.AppInfoActivity
import com.example.androidDeviceDetails.activities.BatteryActivity
import com.example.androidDeviceDetails.activities.LocationActivity
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.viewModel.AppInfoViewModel
import com.example.androidDeviceDetails.viewModel.BatteryViewModel
import com.example.androidDeviceDetails.viewModel.LocationViewModel
import com.example.androidDeviceDetails.viewModel.NetworkUsageViewModel

abstract class BaseViewModel {
    abstract fun <T> onData(outputList: ArrayList<T>)

    companion object {
        fun getViewModel(
            type: String,
            binding: ViewBinding,
            context: Context
        ): BaseViewModel {
            return when (type) {
                BatteryActivity.NAME -> BatteryViewModel(binding as ActivityBatteryBinding, context)
                AppInfoActivity.NAME -> AppInfoViewModel(binding as ActivityAppInfoBinding, context)
                LocationActivity.NAME -> LocationViewModel(
                    binding as ActivityLocationBinding,
                    context
                )
                else -> NetworkUsageViewModel(binding as ActivityAppDataBinding, context)
            }
        }
    }
}