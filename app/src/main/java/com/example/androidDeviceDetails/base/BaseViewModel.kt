package com.example.androidDeviceDetails.base

import android.content.Context
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.databinding.DateTimePickerBinding
import com.example.androidDeviceDetails.ui.AppInfoActivity
import com.example.androidDeviceDetails.ui.BatteryActivity
import com.example.androidDeviceDetails.ui.NetworkUsageActivity
import com.example.androidDeviceDetails.viewModel.AppInfoViewModel
import com.example.androidDeviceDetails.viewModel.BatteryViewModel
import com.example.androidDeviceDetails.viewModel.NetworkUsageViewModel
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseViewModel {
    abstract fun <T> onDone(outputList: ArrayList<T>)

    companion object {
        fun getViewModel(type: String, binding: Any?, context: Context): BaseViewModel? {
            return when (type) {
                BatteryActivity.NAME -> BatteryViewModel(binding as ActivityBatteryBinding, context)
                AppInfoActivity.NAME -> AppInfoViewModel(binding as ActivityAppInfoBinding, context)
                NetworkUsageActivity.NAME -> NetworkUsageViewModel(
                    binding as ActivityAppDataBinding,
                    context
                )
                else -> null
            }
        }
    }

    fun updateDateTimeUI(
        startCalendar: Calendar, endCalendar: Calendar, binding: DateTimePickerBinding
    ) {
        val simpleDateFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
        binding.apply {
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

    open fun isLoading(dateTimePickerBinding: DateTimePickerBinding, isVisible: Boolean) {
        dateTimePickerBinding.root.post { dateTimePickerBinding.progressBar.isVisible = isVisible }
    }

    open fun filter(type: Int) {}

    open fun sort(type: Int) {}
}