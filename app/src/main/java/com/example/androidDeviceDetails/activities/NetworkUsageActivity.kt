package com.example.androidDeviceDetails.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.AppController
import com.example.androidDeviceDetails.controller.NetworkUsageController
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageEntity
import java.util.*

class NetworkUsageActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var networkUsageBinding: ActivityAppDataBinding
    private lateinit var networkUsageController: AppController<ActivityAppDataBinding,AppNetworkUsageEntity>
    private lateinit var startCalendar: Calendar
    private lateinit var endCalendar: Calendar


    companion object {
        const val NAME = "network"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkUsageBinding = DataBindingUtil.setContentView(this, R.layout.activity_app_data)
        networkUsageController = AppController(NAME,networkUsageBinding,this)
        startCalendar= Calendar.getInstance()
        startCalendar.set(Calendar.HOUR,0)
        startCalendar.set(Calendar.MINUTE,0)
        endCalendar= Calendar.getInstance()
        networkUsageBinding.apply {
            startTime.setOnClickListener(this@NetworkUsageActivity)
            startDate.setOnClickListener(this@NetworkUsageActivity)
            endTime.setOnClickListener(this@NetworkUsageActivity)
            endDate.setOnClickListener(this@NetworkUsageActivity)
        }
        startCalendar.add(Calendar.DAY_OF_MONTH, -1)
        networkUsageController.cook(TimeInterval(startCalendar.timeInMillis,endCalendar.timeInMillis))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startTime -> setStartTime()
            R.id.startDate -> setStartDate()
            R.id.endTime -> setEndTime()
            R.id.endDate -> setEndDate()
        }
    }

    private fun setStartTime() {
        val hour = startCalendar.get(Calendar.HOUR)
        val minute =startCalendar.get(Calendar.MINUTE)
        TimePickerDialog(
            this@NetworkUsageActivity, startTimePickerListener, hour, minute,
            DateFormat.is24HourFormat(this)
        ).show()
    }

    private val startTimePickerListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            startCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
            startCalendar[Calendar.MINUTE] = minute
            networkUsageController.cook(TimeInterval(startCalendar.timeInMillis,endCalendar.timeInMillis))
        }

    private fun setStartDate() {
        val day = startCalendar.get(Calendar.DAY_OF_MONTH)
        val month = startCalendar.get(Calendar.MONTH)
        val year = startCalendar.get(Calendar.YEAR)
        DatePickerDialog(
            this@NetworkUsageActivity,
            startDatePickerListener,
            year,
            month,
            day
        ).show()
    }

    private var startDatePickerListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            startCalendar.set(year, month, dayOfMonth)
            networkUsageController.cook(TimeInterval(startCalendar.timeInMillis,endCalendar.timeInMillis))
        }

    private fun setEndTime() {
        val hour = startCalendar.get(Calendar.HOUR)
        val minute = startCalendar.get(Calendar.MINUTE)
        TimePickerDialog(
            this@NetworkUsageActivity, endTimePickerListener, hour, minute,
            DateFormat.is24HourFormat(this)
        ).show()
    }

    private val endTimePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        endCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
        endCalendar[Calendar.MINUTE] = minute
        networkUsageController.cook(TimeInterval(startCalendar.timeInMillis,endCalendar.timeInMillis))
    }

    private fun setEndDate() {
        val day = endCalendar.get(Calendar.DAY_OF_MONTH)
        val month = endCalendar.get(Calendar.MONTH)
        val year = endCalendar.get(Calendar.YEAR)
        DatePickerDialog(
            this@NetworkUsageActivity,
            endDatePickerListener,
            year,
            month,
            day
        ).show()
    }

    private var endDatePickerListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            endCalendar.set(year, month, dayOfMonth)
            networkUsageController.cook(TimeInterval(startCalendar.timeInMillis,endCalendar.timeInMillis))
        }


}