package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.controllers.SignalController
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.Signal
import com.example.androidDeviceDetails.models.WifiRaw
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class SignalStrengthActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var controller: SignalController

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


    private lateinit var filter: Button
    private lateinit var binding: ActivitySignalStrengthBinding

    private var fromTimestamp: Long = 0
    private var toTimestamp: Long = 0
    private var toggle = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        controller = SignalController(binding,this,lifecycleOwner = this)

        controller.observeSignal(Signal.CELLULAR.ordinal)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellularStrength -> {
                    binding.gauge.setMaxValue(-50f)
                    binding.gauge.setMinValue(-150f)
                    controller.observeSignal(Signal.CELLULAR.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifiStrength -> {
                    binding.gauge.setMaxValue(0f)
                    binding.gauge.setMinValue(-100f)
                    controller.observeSignal(Signal.WIFI.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        binding.startTime.setOnClickListener {
            toggle = 1
            getDateTimeCalender()
            DatePickerDialog(this, this, year, month, day).show()
        }
        binding.endTime.setOnClickListener {
            toggle = 2
            getDateTimeCalender()
            DatePickerDialog(this, this, year, month, day).show()
        }

    }


    private fun getDateTimeCalender() {
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
        TimePickerDialog(this, this, hour, minute, true).show()
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
            binding.startTime.text =
                "    $savedDay/${savedMonth + 1}/$savedYear  $savedHour:$savedMinute"
        }
        if (toggle == 2) {
            toTimestamp = getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour, savedMinute)
            binding.endTime.text =
                "    $savedDay/${savedMonth + 1}/$savedYear  $savedHour:$savedMinute"
        }
    }

    private fun getTimeStamp(day: Int, month: Int, year: Int, hour: Int, minute: Int): Long {
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
}