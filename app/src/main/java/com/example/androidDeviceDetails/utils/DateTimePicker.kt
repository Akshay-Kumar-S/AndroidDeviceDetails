package com.example.androidDeviceDetails.utils

import android.content.Context
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.databinding.DateTimePickerBinding

class DateTimePicker(
    context: Context, pickerBinding: DateTimePickerBinding,
    setTime: (Context, Int) -> Unit, setDate: (Context, Int) -> Unit
) {
    init {
        pickerBinding.startTime.setOnClickListener { setTime(context, R.id.startTime) }
        pickerBinding.startDate.setOnClickListener { setDate(context, R.id.startDate) }
        pickerBinding.endTime.setOnClickListener { setTime(context, R.id.endTime) }
        pickerBinding.endDate.setOnClickListener { setDate(context, R.id.endDate) }
    }
}