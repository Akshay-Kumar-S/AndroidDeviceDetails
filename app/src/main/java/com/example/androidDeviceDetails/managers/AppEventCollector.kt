package com.example.androidDeviceDetails.managers

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.batteryModels.AppEventRaw
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppEventCollector(context: Context) : BaseCollector() {
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
                val appUsageData = AppEventRaw(
                    timeStamp = evt.timeStamp,
                    packageName = evt.packageName
                )
                GlobalScope.launch(Dispatchers.IO) { db.appEventDao().insertAll(appUsageData) }
            }
        }
    }

}
