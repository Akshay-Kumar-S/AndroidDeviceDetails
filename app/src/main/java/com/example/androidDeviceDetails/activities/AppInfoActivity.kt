package com.example.androidDeviceDetails.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.AppController
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.managers.AppInfoManager
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfoModels.EventType
import com.example.androidDeviceDetails.services.CollectorService
import com.example.androidDeviceDetails.utils.Utils
import java.text.SimpleDateFormat
import java.util.*


class AppInfoActivity : AppCompatActivity() {

    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var binding: ActivityAppInfoBinding
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var startTimeFlag: Boolean = true
    private lateinit var controller: AppController<ActivityAppInfoBinding,AppInfoCookedData>

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("HH:mm',' dd/MM/yyyy")

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_info_menu, menu)
        return true
    }

    companion object {
        const val NAME = "appInfo"
    }

    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val title = findViewById<TextView>(R.id.filter_text)
        when (item.itemId) {
            R.id.spinner_all -> {
                title.text = "All"
                binding.statisticsContainer.tag = "${EventType.ALL_EVENTS.ordinal}"
            }
            R.id.spinner_enrolled -> {
                title.text = "Enrolled"
                binding.statisticsContainer.tag = "${EventType.APP_ENROLL.ordinal}"

            }
            R.id.spinner_installed -> {
                title.text = "Installed"
                binding.statisticsContainer.tag = "${EventType.APP_INSTALLED.ordinal}"
            }
            R.id.spinner_updated -> {
                title.text = "Updated"
                binding.statisticsContainer.tag = "${EventType.APP_UPDATED.ordinal}"
            }
            R.id.spinner_uninstalled -> {
                title.text = "Uninstalled"
                binding.statisticsContainer.tag = "${EventType.APP_UNINSTALLED.ordinal}"
            }
            R.id.filter_text -> {
            }
            else -> super.onSupportNavigateUp()
        }
        if (startTime != 0L && endTime != 0L) {
            controller.cook(TimeInterval(startTime, endTime))
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_info)
        controller = AppController(NAME,binding,this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.statisticsContainer.isVisible = false
        binding.appInfoListView.isEnabled = false

        startTime = Utils.loadPreviousDayTime()
        endTime = System.currentTimeMillis()
        controller.cook(TimeInterval(startTime, endTime))
        binding.startdateView.text = simpleDateFormat.format(startTime)
        binding.enddateView.text = simpleDateFormat.format(endTime)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, CollectorService::class.java))
        } else {
            this.startService(Intent(this, CollectorService::class.java))
        }

        binding.startdateView.setOnClickListener {
            startTimeFlag = true
            val datePickerDialog = getCalendarDialog()
            if (endTime != 0L) {
                datePickerDialog.datePicker.maxDate = endTime
            } else {
                datePickerDialog.datePicker.maxDate = Date().time
            }
            datePickerDialog.show()
        }

        binding.enddateView.setOnClickListener {
            startTimeFlag = false
            val datePickerDialog = getCalendarDialog()
            if (startTime != 0L) {
                datePickerDialog.datePicker.minDate = startTime
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            } else {
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            }
            datePickerDialog.show()
        }
    }

    private val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute
        val time = simpleDateFormat.format(calendar.timeInMillis)
        if (startTimeFlag) {
            startTime = calendar.timeInMillis
            if (startTime < endTime || endTime == 0L) {
                binding.startdateView.text = time
                if (startTime != 0L && endTime != 0L)
                    controller.cook(TimeInterval(startTime, endTime))
            } else {
                Toast.makeText(
                    this,
                    "Start time must be lower than end time",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            endTime = calendar.timeInMillis
            if (startTime < endTime || startTime == 0L) {
                binding.enddateView.text = time
                if (startTime != 0L && endTime != 0L)
                    controller.cook(TimeInterval(startTime, endTime))
            } else {
                Toast.makeText(
                    this,
                    "End time must be greater than start time",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private val datePickerListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this@AppInfoActivity, timePickerListener, hour, minute,
                DateFormat.is24HourFormat(this)
            )
            timePickerDialog.show()
        }

    private fun getCalendarDialog(): DatePickerDialog {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        return DatePickerDialog(
            this@AppInfoActivity,
            datePickerListener,
            year,
            month,
            day
        )
    }

    fun deleteApp(view: View) {
        AppInfoManager.deleteApp(view, packageManager, this)
    }

}