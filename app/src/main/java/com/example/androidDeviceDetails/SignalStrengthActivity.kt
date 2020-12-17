package com.example.androidDeviceDetails

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.WifiRaw
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SignalStrengthActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private var db = RoomDB.getDatabase()!!

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0
    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0


    private lateinit var binding: ActivitySignalStrengthBinding
    private var cellStrength: Int = -100
    private var wifiStrength: Int = -80
    private var linkspeed: String = "0"
    private var cellInfoType: String = "LTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        updateGauge()
        db.wifiDao().getLastLive().observe(this) {
            if(it!=null)updateWifiGauge(it)
        }
        db.cellularDao().getLastLive().observe(this) {
            if(it!=null)updateCellularGauge(it)
        }
        pickDate()

    }

    private fun getDateTimeCalender() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun pickDate() {
        val txt: TextView
        txt = findViewById(R.id.cellular)
        txt.setOnClickListener {
            getDateTimeCalender()
            DatePickerDialog(this, this, year, month, day).show()
        }

    }

    private fun updateGauge() {
        binding.gaugeCellular.moveToValue(cellStrength.toFloat())
        binding.gaugeCellular.setLowerText(cellInfoType)
        binding.gaugeCellular.setUpperText(cellStrength.toString())
        binding.gaugeWifi.moveToValue(wifiStrength.toFloat())
        binding.gaugeWifi.setLowerText(linkspeed)
        binding.gaugeWifi.setUpperText(wifiStrength.toString())
    }

    private fun updateWifiGauge(wifiRaw: WifiRaw) {
        Log.d("test", "updateWifiGauge: ")
        wifiStrength = wifiRaw.strength!!
        linkspeed = wifiRaw.linkSpeed.toString()
        binding.gaugeWifi.moveToValue(wifiStrength.toFloat())
        binding.gaugeWifi.setLowerText(linkspeed)
        binding.gaugeWifi.setUpperText(wifiStrength.toString())
    }

    private fun updateCellularGauge(cellularRaw: CellularRaw) {
        Log.d("tag", "updateCellularGauge: ")
        cellStrength = cellularRaw.strength!!
        cellInfoType = cellularRaw.type.toString()
        binding.gaugeCellular.moveToValue(cellStrength.toFloat())
        binding.gaugeCellular.setLowerText(cellInfoType)
        binding.gaugeCellular.setUpperText(cellStrength.toString())
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

        GlobalScope.launch {
            val wifiList=  db.wifiDao().getAllBetween(
                getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour, savedMinute),
                getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour + 1, savedMinute)
            )
          val cellularList=db.cellularDao().getAllBetween(
              getTimeStamp(
                  savedDay,
                  savedMonth + 1,
                  savedYear,
                  savedHour,
                  savedMinute
              ),
              getTimeStamp(savedDay, savedMonth + 1, savedYear, savedHour + 1, savedMinute)
          )
            Log.d(
                "calenderdata",
                cellularList.toString()
            )
            Log.d(
                "calenderdata1",
                wifiList.toString()
            )
            runOnUiThread {
                val text:TextView=findViewById(R.id.wifi)
                text.text=cellularList.toString()
            }

        }

    }

    fun getTimeStamp(day: Int, month: Int, year: Int, hour: Int, minute: Int): Long {
        val hexString = Integer.toHexString(hour * 60 * 60 + minute * 60)
        val str_date = "$day-$month-$year"
        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = formatter.parse(str_date) as Date
        var timestamp = date.time
        Log.d("calender", "${hexString.toLong(16)}")
        timestamp = timestamp / 1000 + hexString.toLong(16)
        Log.d("calender", "${timestamp * 1000}")
        return timestamp*1000
    }
}