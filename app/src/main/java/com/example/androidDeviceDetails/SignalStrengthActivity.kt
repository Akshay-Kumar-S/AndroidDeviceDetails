package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.WifiRaw
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class SignalStrengthActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
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

    private lateinit var filter: Button
    private lateinit var binding: ActivitySignalStrengthBinding
    private var maxValue = -50
    private var minValue = -150

    private var fromTimestamp: Long = 0
    private var toTimestamp: Long = 0
    private var toggle = 0
    private var strength: Int = -100
    private var linkspeed: String = "0"
    private var cellInfoType: String = "LTE"
    private var text2 = cellInfoType
    private var text1 = "Type"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        updateGauge()

        db.cellularDao().getLastLive().observe(this) {
            updateCellularGauge(it)
        }
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellularStrength -> {
                    minValue = -150
                    maxValue = -50
                    text1 = "Type"
                    text2 = "LTE"
                    db.cellularDao().getLastLive().observe(this) {
                        updateCellularGauge(it)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifiStrength -> {
                    maxValue = 0
                    minValue = -100
                    text1 = "Linkspeed"
                    text2 = "0 MHz"
                    db.wifiDao().getLastLive().observe(this) {
                        updateWifiGauge(it)
                    }
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


    @SuppressLint("SetTextI18n")
    private fun updateGauge() {
        binding.gauge.setMaxValue(maxValue.toFloat())
        binding.gauge.setMinValue(minValue.toFloat())
        binding.gauge.moveToValue(strength.toFloat())
        binding.gauge.setLowerText(strength.toString())
        binding.textStrength.text = "${strength.toString()} dBm"
        binding.textView3.text = text1
        binding.textView4.text = text2
    }

    private fun updateWifiGauge(wifiRaw: WifiRaw) {
        Log.d("test", "updateWifiGauge: ")
        strength = wifiRaw.strength!!
        linkspeed = wifiRaw.linkSpeed.toString()
        text2 = "$linkspeed MHz"
        updateGauge()
    }

    private fun updateCellularGauge(cellularRaw: CellularRaw) {
        Log.d("tag", "updateCellularGauge: ")
        strength = cellularRaw.strength!!
        cellInfoType = cellularRaw.type.toString()
        text2 = cellInfoType
        updateGauge()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        getDateTimeCalender()
        TimePickerDialog(this, this, hour, minute, true).show()
    }

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
            binding.endTime.text = "    $savedDay/${savedMonth + 1}/$savedYear  $savedHour:$savedMinute"
        }

//        GlobalScope.launch {
//            val wifiList=  db.wifiDao().getAllBetween(
//                getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour, savedMinute),
//                getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour + 1, savedMinute)
//            )
//            val cellularList=db.cellularDao().getAllBetween(
//                getTimeStamp(
//                    savedDay,
//                    savedMonth + 1,
//                    savedYear,
//                    savedHour,
//                    savedMinute
//                ),
//                getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour + 1, savedMinute)
//            )
//            Log.d(
//                "calenderdata",
//                cellularList.toString()
//            )
//            Log.d(
//                "calenderdata1",
//                wifiList.toString()
//            )
//            runOnUiThread {
//                val text:TextView=findViewById(R.id.textView3)
//                text.text=cellularList.toString()
//            }
//        }
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
