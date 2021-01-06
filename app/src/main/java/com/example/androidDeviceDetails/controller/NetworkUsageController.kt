package com.example.androidDeviceDetails.controller

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import com.example.androidDeviceDetails.ICookingDone
import com.example.androidDeviceDetails.cooker.NetworkUsageCooker
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageEntity
import com.example.androidDeviceDetails.viewModel.NetworkUsageViewModel
import java.util.*

class NetworkUsageController(
    val context: Context,
    networkUsageBinding: ActivityAppDataBinding
) {
    val startCalendar: Calendar = Calendar.getInstance()
    val endCalendar: Calendar = Calendar.getInstance()
    private val networkUsageViewModel = NetworkUsageViewModel(networkUsageBinding,context)
    fun setCooker() {
        networkUsageViewModel.updateTextViews(startCalendar, endCalendar)
        startCalendar.set(Calendar.SECOND, 0)
        endCalendar.set(Calendar.SECOND, 0)
        startCalendar.set(Calendar.MILLISECOND, 0)
        endCalendar.set(Calendar.MILLISECOND, 0)
        NetworkUsageCooker().cook(
            TimeInterval(0L,0L),onCookingDone
        )
    }

    private val onCookingDone = object : ICookingDone<AppNetworkUsageEntity> {
//        override fun onNoData() = networkUsageViewModel.onNoData()
        override fun onDone(outputList: ArrayList<AppNetworkUsageEntity>) =
            networkUsageViewModel.onData(outputList)
    }

    fun resolveListener(type: Int): Any? {
        when (type) {
            START_TIME ->
                return TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    startCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
                    startCalendar[Calendar.MINUTE] = minute
                    setCooker()
                }
            START_DATE ->
                return DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    startCalendar.set(year, month, dayOfMonth)
                    setCooker()
                }
            END_TIME ->
                return TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    endCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
                    endCalendar[Calendar.MINUTE] = minute
                    setCooker()
                }
            END_DATE ->
                return DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    endCalendar.set(year, month, dayOfMonth)
                    setCooker()
                }
        }
        return null
    }

    companion object {
        const val START_TIME = 0
        const val START_DATE = 1
        const val END_TIME = 2
        const val END_DATE = 3
    }


}

