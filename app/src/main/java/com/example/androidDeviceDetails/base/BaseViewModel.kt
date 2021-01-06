package com.example.androidDeviceDetails.base

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.activities.AppInfoActivity
import com.example.androidDeviceDetails.activities.BatteryActivity
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.utils.Utils
import com.example.androidDeviceDetails.viewModel.AppInfoViewModel
import com.example.androidDeviceDetails.viewModel.BatteryViewModel
import com.example.androidDeviceDetails.viewModel.NetworkUsageViewModel
import java.text.DecimalFormat
import java.util.*

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

    fun updateTextViews(startCalendar: Calendar, endCalendar: Calendar, view: View) {
        val dec = DecimalFormat("00")

        var startTime = dec.format(startCalendar.get(Calendar.HOUR)) + ":"
        startTime += dec.format(startCalendar.get(Calendar.MINUTE))

        var endTime = dec.format(endCalendar.get(Calendar.HOUR)) + ":"
        endTime += dec.format(endCalendar.get(Calendar.MINUTE))

        var startDate = startCalendar.get(Calendar.DAY_OF_MONTH).toString() + ", "
        startDate += Utils.getMonth(startCalendar.get(Calendar.MONTH)) + " "
        startDate += startCalendar.get(Calendar.YEAR)

        var endDate = endCalendar.get(Calendar.DAY_OF_MONTH).toString() + ", "
        endDate += Utils.getMonth(endCalendar.get(Calendar.MONTH)) + " "
        endDate += endCalendar.get(Calendar.YEAR)


        view.findViewById<TextView>(R.id.startTime).text = startTime
        view.findViewById<TextView>(R.id.startDate).text = startDate
        view.findViewById<TextView>(R.id.endTime).text = endTime
        view.findViewById<TextView>(R.id.endDate).text = endDate
        view.findViewById<TextView>(R.id.startAMPM).text = if (startCalendar.get(Calendar.AM_PM) == 0) "am" else "pm"

    }
}