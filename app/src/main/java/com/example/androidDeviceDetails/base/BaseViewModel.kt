package com.example.androidDeviceDetails.base

import android.content.Context
import com.example.androidDeviceDetails.activities.AppInfoActivity
import com.example.androidDeviceDetails.activities.BatteryActivity
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.viewModel.AppInfoViewModel
import com.example.androidDeviceDetails.viewModel.BatteryViewModel
import com.example.androidDeviceDetails.viewModel.NetworkUsageViewModel

abstract class BaseViewModel {
    abstract fun <T> onData(outputList: ArrayList<T>)
    companion object {
        fun getViewModel(
            type: String,
            binding: Any?,
            context: Context
        ): BaseViewModel {
            return when (type) {
                BatteryActivity.NAME -> BatteryViewModel(binding as ActivityBatteryBinding, context)
                AppInfoActivity.NAME -> AppInfoViewModel(binding as ActivityAppInfoBinding, context)
                else -> NetworkUsageViewModel(binding as ActivityAppDataBinding, context)
            }
        }
    }
}