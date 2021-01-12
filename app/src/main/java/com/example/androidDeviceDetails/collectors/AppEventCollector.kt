package com.example.androidDeviceDetails.collectors

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.batteryModels.AppEventEntity
import com.example.androidDeviceDetails.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *  Implements [BaseCollector]
 *  A time based collector which collects app usage data.
 *  [collect] must be called to save data to the database.
 **/
class AppEventCollector : BaseCollector() {

    override fun start() {
    }

    /**
     * A collector function which saves an entry of [AppEventEntity] if there is
     * an event of [UsageEvents.Event.ACTIVITY_RESUMED] in a time interval
     * of [Utils.COLLECTION_INTERVAL] and the call time ie [System.currentTimeMillis].
     *
     * The final [AppEventEntity] list is saved into [RoomDB.appEventDao].
     *
     * Uses [UsageStatsManager.queryEvents] which requires [android.Manifest.permission.PACKAGE_USAGE_STATS].
     **/
    override fun collect() {
        val usageStatsManager: UsageStatsManager =
            DeviceDetailsApplication.instance.getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = usageStatsManager.queryEvents(
            System.currentTimeMillis() - Utils.COLLECTION_INTERVAL * 60 * 1000,
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
                GlobalScope.launch(Dispatchers.IO) {
                    RoomDB.getDatabase()?.appEventDao()?.insertAll(appUsageData)
                }
            }
        }
    }

    override fun stop() {
    }
}
