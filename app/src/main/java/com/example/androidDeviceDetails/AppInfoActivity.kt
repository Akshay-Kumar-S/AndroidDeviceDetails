package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
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


class AppInfoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val allEvents = 4
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var appList: List<AppInfoCookedData>
    private lateinit var binding: ActivityAppInfoBinding
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var startTimeFlag: Boolean = true
    val context = this
    private var eventFilter = allEvents

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("HH:mm',' dd/MM/yyyy")

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_info_menu, menu)
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val title = findViewById<TextView>(R.id.filter_text)
        when (item.itemId) {
            R.id.spinner_all -> {
                eventFilter = allEvents
                title.text = "All"
            }
            R.id.spinner_enrolled -> {
                eventFilter = EventType.APP_ENROLL.ordinal
                title.text = "Enrolled"

            }
            R.id.spinner_installed -> {
                eventFilter = EventType.APP_INSTALLED.ordinal
                title.text = "Installed"
            }
            R.id.spinner_updated -> {
                eventFilter = EventType.APP_UPDATED.ordinal
                title.text = "Updated"
            }
            R.id.spinner_uninstalled -> {
                eventFilter = EventType.APP_UNINSTALLED.ordinal
                title.text = "Uninstalled"
            }
            else -> super.onSupportNavigateUp()
        }
        if (startTime != 0L && endTime != 0L) {
            setAppIfoData(startTime, endTime)
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.statisticsContainer.isVisible = false
        binding.statsMap.isVisible = false
        binding.appInfoListView.isEnabled = false

        loadPreviousDayTime()
        setAppIfoData(startTime, endTime)
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


//        ArrayAdapter.createFromResource(
//            this,
//            R.array.filter_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            binding.filterSpinner.adapter = adapter
//            binding.filterSpinner.setSelection(allEvents)
//        }
//        binding.filterSpinner.onItemSelectedListener = this
    }

    private fun justifyListViewHeightBasedOnChildren(listView: ListView) {
        val adapter: ListAdapter = listView.adapter ?: return
        val vg: ViewGroup = listView
        val totalHeight: Int
        val listItem: View = adapter.getView(0, null, vg)
        listItem.measure(0, 0)
        totalHeight = listItem.measuredHeight * appList.size
//        for (i in 0 until adapter.count) {
//            val listItem: View = adapter.getView(i, null, vg)
//            listItem.measure(0, 0)
//            totalHeight += listItem.measuredHeight
//        }
        val par: ViewGroup.LayoutParams = listView.layoutParams
        par.height = totalHeight + listView.dividerHeight * (adapter.count - 1)
        listView.layoutParams = par
        listView.requestLayout()
    }

    private fun resetListViewHeight(listView: ListView) {
        val par: ViewGroup.LayoutParams = listView.layoutParams
        par.height = listView.dividerHeight
        listView.layoutParams = par
        listView.requestLayout()
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

    private fun loadPreviousDayTime() {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR] = 0
        cal[Calendar.MINUTE] = 0
//        endTime = cal.timeInMillis
        endTime = System.currentTimeMillis()
        cal.add(Calendar.DAY_OF_MONTH, -1)
        startTime = cal.timeInMillis
    }

    private fun setAppIfoData(startTime: Long, endTime: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            appList = AppStateCooker.createInstance()
                .getAppsBetween(startTime, endTime, applicationContext)
            val db = RoomDB.getDatabase(applicationContext)!!
            for (app in appList) {
                app.packageName = db.appsDao().getPackageByID(app.appId)
            }
            var filteredList = appList.toMutableList()
            if (eventFilter != allEvents) {
                filteredList.removeAll { it.eventType.ordinal != eventFilter }
            }
            filteredList = filteredList.sortedBy { it.appName }.toMutableList()
            filteredList.removeAll { it.packageName == applicationContext.packageName }
            binding.appInfoListView.post {
                binding.appInfoListView.adapter = null
                if (filteredList.isNotEmpty()) {
                    binding.appInfoListView.adapter =
                        AppInfoListAdapter(
                            context,
                            R.layout.appinfo_tile,
                            filteredList
                        )
                    justifyListViewHeightBasedOnChildren(binding.appInfoListView)
                } else {
                    resetListViewHeight(binding.appInfoListView)
                    binding.statsMap.post { binding.statsMap.isVisible = false }
                    binding.statisticsContainer.post {
                        binding.statisticsContainer.isVisible = false
                    }
                }
            }


            val total = appList.size.toDouble()
            val enrolledAppCount =
                appList.groupingBy { it.eventType.ordinal == EventType.APP_ENROLL.ordinal }
                    .eachCount()
            val enrolled = ((enrolledAppCount[true] ?: 0).toDouble().div(total).times(100))

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
                binding.statsMap.isVisible = true
                binding.enrollCount.text = (enrolledAppCount[true] ?: 0).toString()
                binding.installCount.text = (installedAppCount[true] ?: 0).toString()
                binding.updateCount.text = (updateAppCount[true] ?: 0).toString()
                binding.uninstallCount.text = (uninstalledAppCount[true] ?: 0).toString()
            }


        }
    }

    fun deleteApp(view: View) {
        val packageName = view.tag as String
        if (isPackageInstalled(packageName, packageManager)) {
            val packageURI = Uri.parse("package:${packageName}")
            val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
            startActivity(uninstallIntent)
        } else
            Toast.makeText(
                this,
                "App is currently uninstalled",
                Toast.LENGTH_SHORT
            ).show()
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        eventFilter = position
        if (startTime != 0L && endTime != 0L) {
            setAppIfoData(startTime, endTime)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        eventFilter = allEvents
    }
}

