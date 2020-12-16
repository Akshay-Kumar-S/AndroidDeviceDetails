package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.adapters.AppInfoListAdapter
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.managers.AppStateCooker
import com.example.androidDeviceDetails.models.AppInfoCookedData
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.services.CollectorService
import com.example.androidDeviceDetails.utils.EventType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil


class AppInfoActivity : AppCompatActivity() {

    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var appList: List<AppInfoCookedData>
    private lateinit var binding: ActivityAppInfoBinding
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var startTimeFlag: Boolean = true
    val context = this

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("HH:mm',' dd/MM/yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_info)
        binding.statisticsContainer.isVisible = false


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

        binding.appInfoListView.setOnItemClickListener { parent, _, position, _ ->
            val adapter = parent.adapter as AppInfoListAdapter
            val item = adapter.getItem(position)
            Log.d("TAG", "onCreate: $position")
            if (item != null && isPackageInstalled(item.packageName, packageManager)) {
                val infoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
                infoIntent.data = Uri.parse("package:${item.packageName}")
                startActivity(infoIntent)
            } else
                Toast.makeText(
                    this,
                    "App is currently uninstalled, info unavailable",
                    Toast.LENGTH_SHORT
                ).show()

        }
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
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
                    setAppIfoData(startTime, endTime)
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
                    setAppIfoData(startTime, endTime)
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

    @SuppressLint("SimpleDateFormat")
    fun setAppIfoData(startTime: Long, endTime: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            appList = AppStateCooker.createInstance()
                .getAppsBetween(startTime, endTime, applicationContext)
            val db = RoomDB.getDatabase(applicationContext)!!
            for (app in appList) {
                app.packageName = db.appsDao().getPackageByID(app.appId)
            }
            appList = appList.sortedBy { it.appName }
            binding.appInfoListView.post {
                binding.appInfoListView.adapter = null
                binding.appInfoListView.adapter =
                    AppInfoListAdapter(
                        context,
                        R.layout.appinfo_tile,
                        appList
                    )
            }


            val total = appList.size.toDouble()
            val enrolledAppCount =
                appList.groupingBy { it.eventType.ordinal == EventType.APP_ENROLL.ordinal }
                    .eachCount()
            val enrolled = ceil(((enrolledAppCount[true] ?: 0).toDouble().div(total).times(100)))

            val installedAppCount =
                appList.groupingBy { it.eventType.ordinal == EventType.APP_INSTALLED.ordinal }
                    .eachCount()
            val installed = ceil(((installedAppCount[true] ?: 0).toDouble().div(total).times(100)))

            val updateAppCount =
                appList.groupingBy { it.eventType.ordinal == EventType.APP_UPDATED.ordinal }
                    .eachCount()
            val updated = ceil(((updateAppCount[true] ?: 0).toDouble().div(total).times(100)))

            val uninstalledAppCount =
                appList.groupingBy { it.eventType.ordinal == EventType.APP_UNINSTALLED.ordinal }
                    .eachCount()
            val uninstalled =
                ceil(((uninstalledAppCount[true] ?: 0).toDouble().div(total).times(100)))

            binding.updatedProgressBar.progress = (updated.toInt())
            binding.installedProgressBar.progress = (updated + installed).toInt()
            binding.enrollProgressbar.progress = (updated + installed + enrolled.toInt()).toInt()
            binding.uninstalledProgressbar.progress =
                (updated + installed + enrolled + uninstalled).toInt()
            binding.pieChartConstraintLayout.post {
                binding.statisticsContainer.isVisible = true
                binding.enrollCount.text = (enrolledAppCount[true] ?: 0).toString()
                binding.installCount.text = (installedAppCount[true] ?: 0).toString()
                binding.updateCount.text = (updateAppCount[true] ?: 0).toString()
                binding.uninstallCount.text = (uninstalledAppCount[true] ?: 0).toString()
            }


        }
    }
}

