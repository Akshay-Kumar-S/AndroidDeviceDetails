package com.example.androidDeviceDetails.collectors

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.batteryModels.AppEventRaw
import com.example.androidDeviceDetails.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 *  Implements [BaseCollector]
 *  A time based collector which collects app usage data.
 *  [collect] must be called to save data to the database.
 **/
class AppEventCollector(val context: Context) : BaseCollector() {

    /**
     * A collector function which saves an entry of [AppEventRaw] if there is
     * an event of [UsageEvents.Event.ACTIVITY_RESUMED] in a time interval
     * of [Utils.COLLECTION_INTERVAL] and the call time ie [System.currentTimeMillis].
     *
     * The final [AppEventRaw] list is saved into [RoomDB.appEventDao].
     *
     * Uses [UsageStatsManager.queryEvents] which requires [android.Manifest.permission.PACKAGE_USAGE_STATS].
     **/
    override fun collect() {
        val statsManager =
            context.getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = statsManager.queryEvents(
            System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(Utils.COLLECTION_INTERVAL),
            System.currentTimeMillis()
        )
        while (events.hasNextEvent()) {
            val evt = UsageEvents.Event()
            events.getNextEvent(evt)
            if (evt.eventType == 1) {
                val appUsageData = AppEventRaw(evt.timeStamp, evt.packageName)
                GlobalScope.launch { RoomDB.getDatabase()?.appEventDao()?.insert(appUsageData) }
            }
        }
    }
}