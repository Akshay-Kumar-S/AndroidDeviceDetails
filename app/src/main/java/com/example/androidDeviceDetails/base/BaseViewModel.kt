package com.example.androidDeviceDetails.base

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.location.LocationViewModel


abstract class BaseViewModel {
    abstract fun <T> populateList(data: MutableList<T>)
    abstract fun onNoData()

    companion object {
        fun getViewModel(type: String, context: Context, binding: ViewBinding): BaseViewModel {
            return when (type) {
                LocationViewModel.NAME -> LocationViewModel(context,binding as ActivityLocationBinding)
                else -> LocationViewModel(context,binding as ActivityLocationBinding)
            }
        }
    }
}