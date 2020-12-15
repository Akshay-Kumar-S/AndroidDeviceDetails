package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlin.math.log


class AppInfoActivity : AppCompatActivity() {

    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var appList: List<AppInfoCookedData>
    private lateinit var binding: ActivityAppInfoBinding
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var startTimeFlag: Boolean = true
    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_info)
        var day: Int
        var month: Int
        var year: Int
        var hour: Int
        var minute: Int


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, CollectorService::class.java))
        } else {
            this.startService(Intent(this, CollectorService::class.java))
        }

        val timePickerListener = TimePickerDialog.OnTimeSetListener { i, hourOfDay, minute ->
            calendar[Calendar.HOUR_OF_DAY] = hourOfDay
            calendar[Calendar.MINUTE] = minute
            val simpleDateFormat = SimpleDateFormat("HH:mm',' dd/MM/yyyy")
            val time = simpleDateFormat.format(calendar.timeInMillis)
            if (startTimeFlag) {
                startTime = calendar.timeInMillis
                binding.startdateView.text = time
                if (startTime != 0L && endTime != 0L)
                    setAppIfoData(startTime, endTime)
            } else {
                endTime = calendar.timeInMillis
                binding.enddateView.text = time
                if (startTime != 0L && endTime != 0L)
                    setAppIfoData(startTime, endTime)
            }

//            val endTime = startTime + (((((23 * 60) + 59) * 60) + 59) * 1000)

        }

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            hour = calendar.get(Calendar.HOUR)
            minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this@AppInfoActivity, timePickerListener, hour, minute,
                DateFormat.is24HourFormat(this)
            )
            timePickerDialog.show()
        }


        binding.startdateView.setOnClickListener {
            startTimeFlag = true
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            @Suppress("RedundantSamConstructor") val datePickerDialog =
                DatePickerDialog(
                    this@AppInfoActivity,
                    datePickerListener,
                    year,
                    month,
                    day
                )
            datePickerDialog.show()
        }

        binding.enddateView.setOnClickListener {
            startTimeFlag = false
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            @Suppress("RedundantSamConstructor") val datePickerDialog =
                DatePickerDialog(
                    this@AppInfoActivity,
                    datePickerListener,
                    year,
                    month,
                    day
                )
            datePickerDialog.show()
        }
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
            var installed = 0.toDouble()
            var updated = 0.toDouble()
            var uninstalled = 0.toDouble()
            var enrolled=0.toDouble()

            val total = appList.size.toDouble()
            var eapps = appList.groupingBy { it.eventType.ordinal == EventType.APP_ENROLL.ordinal }
                .eachCount()
            try {
                enrolled = ceil((eapps[true]?.toDouble()?.div(total)!!) * 100)
            }catch (e:Exception){}
            var x = appList.groupingBy { it.eventType.ordinal == EventType.APP_INSTALLED.ordinal }
                .eachCount()
            try {
                installed = ceil((x[true]?.toDouble()?.div(total)!!) * 100)

            } catch (e: Exception) {
            }
            x = appList.groupingBy { it.eventType.ordinal == EventType.APP_UPDATED.ordinal }
                .eachCount()
            try {
                updated = ceil((x[true]?.toDouble()?.div(total)!!) * 100)
            } catch (e: Exception) {
            }
            x = appList.groupingBy { it.eventType.ordinal == EventType.APP_UNINSTALLED.ordinal }
                .eachCount()
            try {
                uninstalled = ceil((x[true]?.toDouble()?.div(total)!!) * 100)
            } catch (e: Exception) {
            }

            binding.enrollProgressbar.post {
                binding.enrollProgressbar.setOnClickListener {
                    Log.d("TAG", "setAppIfoData: $eapps[true]")
                    Log.d("TAG", "setAppIfoData: $eapps[true]")
                    Log.d("TAG", "setAppIfoData: $eapps[true]")
                    Log.d("TAG", "setAppIfoData: $eapps[true]")

                }
            }
            Log.d("TAG", "enrolled: $enrolled")
            Log.d("TAG", "installed: $installed")
            Log.d("TAG", "updated: $updated")
            Log.d("TAG", "uninstalled: $uninstalled")
            Log.d("TAG", "listsize: ${appList.size}")





            binding.updatedProgressBar.progress = (updated.toInt())
            binding.installedProgressBar.progress = (updated + installed).toInt()
            binding.enrollProgressbar.progress = (updated + installed + enrolled.toInt()).toInt()
            binding.uninstalledProgressbar.progress = (updated + installed + enrolled + uninstalled).toInt()

        }
    }
}

