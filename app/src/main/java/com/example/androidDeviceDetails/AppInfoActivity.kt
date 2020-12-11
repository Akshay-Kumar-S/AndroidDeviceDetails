package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.models.AppHistory
import com.example.androidDeviceDetails.models.CookedData
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.services.CollectorService
import com.example.androidDeviceDetails.utils.EventType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AppInfoActivity : AppCompatActivity() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, CollectorService::class.java))
        } else {
            this.startService(Intent(this, CollectorService::class.java))
        }

        var appList: List<CookedData>
        val button = findViewById<Button>(R.id.button)
        val text = findViewById<TextView>(R.id.textView)
        var datePicker: DatePicker = findViewById(R.id.date_Picker)
        var today: Calendar = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
        { _, year, month, day ->
            today.set(year, month, day)
            today[Calendar.HOUR_OF_DAY] = 0
            today[Calendar.MINUTE] = 0
            today[Calendar.SECOND] = 0
            val startTime = today.timeInMillis
            val endTime = startTime + (((((23 * 60) + 59) * 60) + 59) * 1000)
            var swapText = ""
            GlobalScope.launch(Dispatchers.IO) {
                appList = getAppsBetween(startTime, endTime)
                val eventArray = EventType.values()
                for (app in appList) {
                    swapText =
                        swapText + app.appName + " | " + app.eventType.name + "\n"
                }
                runOnUiThread {
                    text.text = swapText
                    Toast.makeText(
                        this@AppInfoActivity,
                        appList.size.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

        }


        /*button?.setOnClickListener()
        {
            GlobalScope.launch(Dispatchers.IO) {
                appList = getChangedApps()
                var swaptext = ""
                val eventArray = EventType.values()
                for (app in appList) {
                    swaptext =
                        swaptext + app.appTitle + " | " + app.versionName + " | " + app.currentVersionCode + " | " + app.appSize + " | " + eventArray[app.eventType!!] + "\n"
                }
                runOnUiThread {
                    text.text = swaptext
                    Toast.makeText(
                        this@AppInfoActivity,
                        appList.size.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }*/
    }


    private fun getAppsBetween(startTime: Long, endTime: Long): List<CookedData> {
        val db = RoomDB.getDatabase(this)!!
        val appList = listOf<CookedData>().toMutableList()
        var allRecords = db.appHistoryDao().getAppsBetween(startTime, endTime)
        val ids = db.appHistoryDao().getIdsBetween(startTime, endTime)
        for (id in ids) {
            var lastRecord = db.appHistoryDao().getLatestRecordBetween(id, startTime, endTime)
            var initialRecord = db.appHistoryDao().getInitialRecordBetween(id, startTime, endTime)
            if (lastRecord.eventType == EventType.APP_ENROLL.ordinal) {
                appList.add(CookedData(lastRecord.appTitle, EventType.APP_ENROLL))
            } else if (lastRecord.eventType == EventType.APP_UNINSTALLED.ordinal) {
                appList.add(CookedData(lastRecord.appTitle, EventType.APP_UNINSTALLED))
            } else if (initialRecord.previousVersionCode != lastRecord.currentVersionCode) {
                appList.add(CookedData(lastRecord.appTitle, EventType.APP_UPDATED))
            }
            if (initialRecord.previousVersionCode == EventType.APP_INSTALLED.ordinal.toLong()) {
                appList.add(CookedData(lastRecord.appTitle, EventType.APP_INSTALLED))
            }
        }

        return appList

    }

    private fun getChangedApps(): List<AppHistory> {
        val db = RoomDB.getDatabase(this)!!
        val apps = db.appsDao().getAll()
        val appList = listOf<AppHistory>().toMutableList()
        appList.clear()
        for (app in apps) {
            val initialAppState = db.appHistoryDao().getInitialData(app.uid)
            val latestAppState = db.appHistoryDao().getLastRecord(app.uid)

            if (latestAppState.currentVersionCode != null) {
                if (initialAppState.currentVersionCode!! < latestAppState.currentVersionCode!!) {
                    appList.add(latestAppState)

                } else if (initialAppState.appTitle!! < latestAppState.appTitle!!) {
                    appList.add(latestAppState)

                } else if (latestAppState.eventType == EventType.APP_UNINSTALLED.ordinal) {
                    appList.add(latestAppState)

                } else if (initialAppState.currentVersionCode == latestAppState.currentVersionCode && latestAppState.timestamp != -1L) {
                    appList.add(latestAppState)

                }
            }
        }
        return appList
    }
}