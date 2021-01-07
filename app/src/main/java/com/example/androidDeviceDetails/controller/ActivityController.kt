package com.example.androidDeviceDetails.controller

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.androidDeviceDetails.BottomSheet
import com.example.androidDeviceDetails.ICookingDone
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.managers.AppInfoManager
import com.example.androidDeviceDetails.models.TimeInterval
import java.util.*

class ActivityController<T, MT>(
    dataType: String,
    binding: T,
    var context: Context,
    private val dateTimePickerView: View? = null,
    val supportFragmentManager: FragmentManager
) {

    private var cooker: BaseCooker = BaseCooker.getCooker(dataType)
    private var viewModel: BaseViewModel = BaseViewModel.getViewModel(dataType, binding, context)
    private var startCalendar: Calendar = Calendar.getInstance()
    private var endCalendar: Calendar = Calendar.getInstance()

    private val onCookingDone = object : ICookingDone<MT> {
        override fun onDone(outputList: ArrayList<MT>) =
            viewModel.onData(outputList)
    }

    init {
        startCalendar.set(Calendar.HOUR, 0)
        startCalendar.set(Calendar.MINUTE, 0)
        startCalendar.add(Calendar.DAY_OF_MONTH, -1)
        showInitialData()
        cook(
            TimeInterval(
                startCalendar.timeInMillis,
                endCalendar.timeInMillis
            )
        )
    }

    fun cook(timeInterval: TimeInterval) {
        cooker.cook(timeInterval, onCookingDone)
    }



    fun setStartTime(context: Context) {
        val hour = startCalendar.get(Calendar.HOUR)
        val minute = startCalendar.get(Calendar.MINUTE)
        TimePickerDialog(
            context, startTimePickerListener, hour, minute,
            DateFormat.is24HourFormat(context)
        ).show()
    }

    private val startTimePickerListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            startCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
            startCalendar[Calendar.MINUTE] = minute
            validateTimeInterval()
        }

    fun setStartDate(context: Context) {
        val day = startCalendar.get(Calendar.DAY_OF_MONTH)
        val month = startCalendar.get(Calendar.MONTH)
        val year = startCalendar.get(Calendar.YEAR)
        val startDatePicker = DatePickerDialog(
            context,
            startDatePickerListener,
            year,
            month,
            day
        )
        startDatePicker.datePicker.maxDate = endCalendar.timeInMillis
        startDatePicker.show()
    }

    private var startDatePickerListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            startCalendar.set(year, month, dayOfMonth)
            validateTimeInterval()
        }

    fun setEndTime(context: Context) {
        val hour = startCalendar.get(Calendar.HOUR)
        val minute = startCalendar.get(Calendar.MINUTE)
        TimePickerDialog(
            context, endTimePickerListener, hour, minute,
            DateFormat.is24HourFormat(context)
        ).show()
    }

    private val endTimePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        endCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
        endCalendar[Calendar.MINUTE] = minute
        validateTimeInterval()
    }

    fun setEndDate(context: Context) {
        val day = endCalendar.get(Calendar.DAY_OF_MONTH)
        val month = endCalendar.get(Calendar.MONTH)
        val year = endCalendar.get(Calendar.YEAR)
        val endDatePicker = DatePickerDialog(
            context,
            endDatePickerListener,
            year,
            month,
            day
        )
        endDatePicker.datePicker.minDate = startCalendar.timeInMillis
        endDatePicker.datePicker.maxDate = System.currentTimeMillis()
        endDatePicker.show()
    }

    private var endDatePickerListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            endCalendar.set(year, month, dayOfMonth)
            validateTimeInterval()
        }

    private fun validateTimeInterval() {
        if (startCalendar.timeInMillis < endCalendar.timeInMillis) {
            BottomSheet(onApply = { apply() }).show(supportFragmentManager, "Apply")
            viewModel.updateTextViews(startCalendar, endCalendar, dateTimePickerView!!)
        } else {
            Toast.makeText(
                context,
                "Enter valid time interval",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun apply() {
        startCalendar.set(Calendar.SECOND, 0)
        endCalendar.set(Calendar.SECOND, 0)
        startCalendar.set(Calendar.MILLISECOND, 0)
        endCalendar.set(Calendar.MILLISECOND, 0)
        cook(
            TimeInterval(
                startCalendar.timeInMillis,
                endCalendar.timeInMillis
            )
        )
    }

    fun showInitialData() {
        viewModel.updateTextViews(startCalendar, endCalendar, dateTimePickerView!!)
    }

    fun filterData() {
        viewModel.onData(AppInfoManager.appList)
    }
}

