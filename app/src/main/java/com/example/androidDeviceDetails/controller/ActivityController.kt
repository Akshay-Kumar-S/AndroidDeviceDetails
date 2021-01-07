package com.example.androidDeviceDetails.controller

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.example.androidDeviceDetails.BottomSheet
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.DateTimePickerBinding
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.managers.AppInfoManager
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.utils.Utils
import java.util.*

class ActivityController<T>(
    dataType: String,
    binding: ViewBinding,
    var context: Context,
    private val dateTimePickerView: DateTimePickerBinding,
    val supportFragmentManager: FragmentManager
) {

    private var cooker: BaseCooker = BaseCooker.getCooker(dataType)
    private var viewModel: BaseViewModel = BaseViewModel.getViewModel(dataType, binding, context)
    private var startCalendar: Calendar = Calendar.getInstance()
    private var endCalendar: Calendar = Calendar.getInstance()
    private var previousStartTime: Long = 0
    private var previousEndTime: Long = 0


    private val onCookingDone = object : ICookingDone<T> {
        override fun onDone(outputList: ArrayList<T>) =
            viewModel.onData(outputList)
    }

    init {
        startCalendar.set(Calendar.HOUR, 0)
        startCalendar.set(Calendar.MINUTE, 0)
        previousStartTime = startCalendar.timeInMillis
        previousEndTime = endCalendar.timeInMillis
        showInitialData()
        cook(
            TimePeriod(
                startCalendar.timeInMillis,
                endCalendar.timeInMillis
            )
        )
    }

    fun cook(timePeriod: TimePeriod) {
        cooker.cook(timePeriod, onCookingDone)
    }


    fun setStartTime(context: Context) {
        val hour = startCalendar.get(Calendar.HOUR)
        val minute = startCalendar.get(Calendar.MINUTE)
        TimePickerDialog(
            context, startTimePickerListener, hour, minute,
            false
        ).show()
    }

    private val startTimePickerListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            startCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
            startCalendar[Calendar.MINUTE] = minute
            if (previousStartTime != startCalendar.timeInMillis)
                validateTimeInterval()
            previousStartTime = startCalendar.timeInMillis
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
            if (previousStartTime != startCalendar.timeInMillis)
                validateTimeInterval()
            previousStartTime = startCalendar.timeInMillis
        }

    fun setEndTime(context: Context) {
        val hour = endCalendar.get(Calendar.HOUR)
        val minute = endCalendar.get(Calendar.MINUTE)
        TimePickerDialog(
            context, endTimePickerListener, hour, minute,
            false
        ).show()
    }

    private val endTimePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        endCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
        endCalendar[Calendar.MINUTE] = minute
        if (previousEndTime != endCalendar.timeInMillis)
            validateTimeInterval()
        previousEndTime = endCalendar.timeInMillis
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
            if (previousEndTime != endCalendar.timeInMillis)
                validateTimeInterval()
            previousEndTime = endCalendar.timeInMillis
        }

    private fun validateTimeInterval() {
        if (startCalendar.timeInMillis < endCalendar.timeInMillis) {
            if ((endCalendar.timeInMillis - startCalendar.timeInMillis) > Utils.COLLECTION_INTERVAL * 60 * 1000) {
                BottomSheet(onApply = { onClickApply() }).show(supportFragmentManager, "Apply")
                viewModel.updateTextViews(startCalendar, endCalendar, dateTimePickerView)
            } else
                Toast.makeText(
                    context,
                    "Time interval should be greater than ${Utils.COLLECTION_INTERVAL} minutes",
                    Toast.LENGTH_SHORT
                ).show()
        } else
            Toast.makeText(context, "Enter valid time interval", Toast.LENGTH_SHORT).show()
    }

    private fun onClickApply() {
        startCalendar.set(Calendar.SECOND, 0)
        endCalendar.set(Calendar.SECOND, 0)
        startCalendar.set(Calendar.MILLISECOND, 0)
        endCalendar.set(Calendar.MILLISECOND, 0)
        cook(
            TimePeriod(
                startCalendar.timeInMillis,
                endCalendar.timeInMillis
            )
        )
    }

    fun showInitialData() {
        viewModel.updateTextViews(startCalendar, endCalendar, dateTimePickerView)
    }

    fun filterView(type: Int) {
        viewModel.filter(type)
    }

    fun sortView(type:Int){
        viewModel.sort(type)
    }
}

