package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SignalStrengthCooker(
    private val signalStrengthBinding: ActivitySignalStrengthBinding,
    val context: Context
) : DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private var db = RoomDB.getDatabase()!!
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0
    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0
    private var fromTimestamp: Long = 0
    private var toTimestamp: Long = 0
    private var toggle = 0

    fun onCreate() {

        signalStrengthBinding.startTime.setOnClickListener {
            toggle = 1
            getDateTimeCalender()
            DatePickerDialog(context, this, year, month, day).show()


        }
        signalStrengthBinding.endTime.setOnClickListener {
            toggle = 2
            getDateTimeCalender()
            DatePickerDialog(context, this, year, month, day).show()
        }
    }

    fun getDateTimeCalender() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        getDateTimeCalender()
        TimePickerDialog(context, this, hour, minute, false).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        Log.d("calender", "$savedDay,$savedMonth,$savedYear")
        Log.d("calender", "$savedHour,$savedMinute")
        if (toggle == 1) {
            fromTimestamp =
                getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour, savedMinute)
            signalStrengthBinding.startTime.text =
                "$savedDay/${savedMonth + 1}/$savedYear  $savedHour:$savedMinute"
        }
        if (toggle == 2) {
            toTimestamp = getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour, savedMinute)
            signalStrengthBinding.endTime.text =
                "$savedDay/${savedMonth + 1}/$savedYear  $savedHour:$savedMinute"
        }
        Log.e("timegettera","$fromTimestamp   $toTimestamp")
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeStamp(day: Int, month: Int, year: Int, hour: Int, minute: Int): Long {
        val hexString = Integer.toHexString(hour * 60 * 60 + minute * 60)
        val str_date = "$day-$month-$year"
        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = formatter.parse(str_date) as Date
        var timestamp = date.time
        Log.d("calender", "${hexString.toLong(16)}")
        timestamp = timestamp / 1000 + hexString.toLong(16)
        Log.d("calender", "${timestamp * 1000}")
        return timestamp * 1000
    }
    fun getFromTimestamp():Long{return fromTimestamp}
    fun getToTimestamp():Long{return toTimestamp}
    fun cookSignalData(
        callback: SignalCookingInterface<CellularRaw>,beginTime:Long,endTime:Long,type:Int
    ) {
        Log.e("path","")
        GlobalScope.launch {
            Log.e("timegetter","$beginTime   $endTime")
            val cellularList = db.cellularDao().getAllBetween(
                beginTime,
               endTime

            )
            val wifiList = db.wifiDao().getAllBetween(
                beginTime,
                endTime
            )
            // cooking
            if (cellularList.isNotEmpty()){
                callback.updateListView(cellularList as ArrayList<CellularRaw>)
            }

        }
    }
}