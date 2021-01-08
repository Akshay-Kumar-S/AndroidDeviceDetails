package com.example.androidDeviceDetails.base

import android.content.Context
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.example.androidDeviceDetails.activities.AppInfoActivity
import com.example.androidDeviceDetails.activities.BatteryActivity
import com.example.androidDeviceDetails.activities.LocationActivity
import com.example.androidDeviceDetails.databinding.*
import com.example.androidDeviceDetails.viewModel.AppInfoViewModel
import com.example.androidDeviceDetails.viewModel.BatteryViewModel
import com.example.androidDeviceDetails.viewModel.LocationViewModel
import com.example.androidDeviceDetails.viewModel.NetworkUsageViewModel
import java.text.SimpleDateFormat
import java.util.*

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
                LocationActivity.NAME -> LocationViewModel(binding as ActivityLocationBinding,context)
                else -> NetworkUsageViewModel(binding as ActivityAppDataBinding, context)
            }
        }
    }


    fun updateTextViews(
        startCalendar: Calendar,
        endCalendar: Calendar,
        dateTimePickerBinding: DateTimePickerBinding
    ) {
        val timeFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
        val dateFormat = SimpleDateFormat("dd, MMM yyyy", Locale.ENGLISH)
        val amPmFormat = SimpleDateFormat("a", Locale.ENGLISH)
        dateTimePickerBinding.startTime.text = timeFormat.format(Date(startCalendar.timeInMillis))
        dateTimePickerBinding.startDate.text = dateFormat.format(Date(startCalendar.timeInMillis))
        dateTimePickerBinding.endTime.text = timeFormat.format(Date(endCalendar.timeInMillis))
        dateTimePickerBinding.endDate.text = dateFormat.format(Date(endCalendar.timeInMillis))
        dateTimePickerBinding.startAMPM.text =
            amPmFormat.format(Date(startCalendar.timeInMillis)).toLowerCase(
                Locale.ENGLISH
            )
        dateTimePickerBinding.endAMPM.text =
            amPmFormat.format(Date(endCalendar.timeInMillis)).toLowerCase(
                Locale.ENGLISH
            )

    }

    open fun isLoading(dateTimePickerBinding: DateTimePickerBinding, isVisible: Boolean){
        dateTimePickerBinding.root.post{dateTimePickerBinding.progressBar.isVisible = isVisible}
    }

    open fun filter(type:Int){}
    open fun sort(type:Int){}
}