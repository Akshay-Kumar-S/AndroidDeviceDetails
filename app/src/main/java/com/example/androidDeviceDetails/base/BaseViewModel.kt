package com.example.androidDeviceDetails.base

import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.example.androidDeviceDetails.databinding.*
import com.example.androidDeviceDetails.ui.*
import com.example.androidDeviceDetails.viewModel.*
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseViewModel {

    abstract fun <T> onComplete(outputList: ArrayList<T>)

    open fun filter(type: Int) {}

    open fun sort(type: Int) {}

    companion object {
        fun getViewModel(type: String, binding: Any?, context: Context): BaseViewModel? =
            when (type) {
                MainActivity.NAME -> MainActivityViewModel(binding as ActivityMainBinding, context)
                BatteryActivity.NAME -> BatteryViewModel(binding as ActivityBatteryBinding, context)
                AppEventActivity.NAME -> AppEventViewModel(
                    binding as ActivityAppInfoBinding,
                    context
                )
                SignalActivity.NAME -> SignalViewModel(binding as ActivitySignalBinding, context)
                LocationActivity.NAME -> LocationViewModel(
                    binding as ActivityLocationBinding, context
                )
                NetworkUsageActivity.NAME -> NetworkUsageViewModel(
                    binding as ActivityAppDataBinding, context
                )
                AppTypeActivity.NAME -> AppTypeViewModel(binding as ActivityAppTypeBinding, context)
                else -> null
            }

        fun getPickerBinding(type: String, binding: ViewBinding): DateTimePickerBinding? =
            when (type) {
                BatteryActivity.NAME -> (binding as ActivityBatteryBinding).pickerBinding
                AppEventActivity.NAME -> (binding as ActivityAppInfoBinding).pickerBinding
                SignalActivity.NAME -> (binding as ActivitySignalBinding).pickerBinding
                LocationActivity.NAME -> (binding as ActivityLocationBinding).locationBottomSheet.dateTimePicker
                NetworkUsageActivity.NAME -> (binding as ActivityAppDataBinding).pickerBinding
                else -> null
            }

    }

    fun updateDateTimeUI(
        startCalendar: Calendar, endCalendar: Calendar, binding: ViewBinding, type: String
    ) {
        getPickerBinding(type, binding)?.apply {
            val simpleDateFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
            startTime.text = simpleDateFormat.format(Date(startCalendar.timeInMillis))
            endTime.text = simpleDateFormat.format(Date(endCalendar.timeInMillis))

            simpleDateFormat.applyPattern("dd, MMM yyyy")
            startDate.text = simpleDateFormat.format(Date(startCalendar.timeInMillis))
            endDate.text = simpleDateFormat.format(Date(endCalendar.timeInMillis))

            simpleDateFormat.applyPattern("a")
            startAMPM.text = simpleDateFormat.format(Date(startCalendar.timeInMillis))
                .toLowerCase(Locale.ENGLISH)
            endAMPM.text =
                simpleDateFormat.format(Date(endCalendar.timeInMillis)).toLowerCase(Locale.ENGLISH)
        }
    }

    open fun isLoading(binding: ViewBinding, enable: Boolean, type: String) {
        getPickerBinding(type, binding)?.apply {
            root.post {
                Log.e("cook", "$enable")
                progressBar.isVisible = enable
            }
        }
    }
}