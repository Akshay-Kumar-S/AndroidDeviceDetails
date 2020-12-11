package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        val text = findViewById<TextView>(R.id.textView)
        val datePicker: DatePicker = findViewById(R.id.date_Picker)
        val today: Calendar = Calendar.getInstance()
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


    }


    private fun getAppsBetween(startTime: Long, endTime: Long): List<CookedData> {
        val db = RoomDB.getDatabase(this)!!
        val appList = listOf<CookedData>().toMutableList()
        val ids = db.appHistoryDao().getIdsBetween(startTime, endTime)
        for (id in ids) {
            val lastRecord = db.appHistoryDao().getLatestRecordBetween(id, startTime, endTime)
            val initialRecord = db.appHistoryDao().getInitialRecordBetween(id, startTime, endTime)
            @Suppress("CascadeIf")
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

}