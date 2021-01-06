package com.example.androidDeviceDetails.managers

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.base.BaseTimeCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.batteryModels.AppEventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AppEventCollector(context: Context) : BaseTimeCollector() {
    override lateinit var timer: Timer
    private var usageStatsManager: UsageStatsManager =
        context.getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager

    override fun collect() {
        val db = RoomDB.getDatabase()!!
        val events = usageStatsManager.queryEvents(
            System.currentTimeMillis() - 1 * 60 * 1000,
            System.currentTimeMillis()
        )
        while (events.hasNextEvent()) {
            val evt = UsageEvents.Event()
            events.getNextEvent(evt)
            if (evt.eventType == 1) {
                val appUsageData = AppEventEntity(
                    timeStamp = evt.timeStamp,
                    packageName = evt.packageName
                )
                GlobalScope.launch(Dispatchers.IO) { db.appEventDao().insertAll(appUsageData) }
            }
        }
    }

    override fun runTimer(intervalInMinuets: Long) {
        timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() = collect()
            },
            0, 1000 * 60 * intervalInMinuets
        )
    }
}
