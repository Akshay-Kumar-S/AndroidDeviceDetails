package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.AppHistoryDao
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfo.EventType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AppEventCooker : BaseCooker() {

    /**
     * Cook data for App Info from the collected data available in the [AppHistoryDao] database for
     * the requested time interval.
     * >
     * Overrides : [cook] in [BaseCooker]
     * @param time data class object that contains start time and end time.
     * @param iCookingDone A callback that accepts the cooked list once cooking is done
     */
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        GlobalScope.launch(Dispatchers.IO) {
            val db = RoomDB.getDatabase()!!
            val appList = arrayListOf<AppInfoCookedData>()
            var evt: AppInfoCookedData? = null
            val ids = db.appHistoryDao().getIdsBetween(time.startTime, time.endTime)
            for (id in ids) {
                val lastRecord =
                    db.appHistoryDao().getLatestRecordBetween(id, time.startTime, time.endTime)
                val initialRecord =
                    db.appHistoryDao().getInitialRecordBetween(id, time.startTime, time.endTime)

                if (lastRecord.eventType == EventType.APP_ENROLL.ordinal) {
                    appList.add(
                        AppInfoCookedData(
                            lastRecord.appTitle, EventType.APP_ENROLL,
                            lastRecord.currentVersionCode, lastRecord.appId, lastRecord.isSystemApp
                        )
                    )
                    continue
                } else if (lastRecord.eventType == EventType.APP_UNINSTALLED.ordinal) {
                    appList.add(
                        AppInfoCookedData(
                            lastRecord.appTitle, EventType.APP_UNINSTALLED,
                            lastRecord.previousVersionCode, lastRecord.appId, lastRecord.isSystemApp
                        )
                    )
                    continue
                } else if (initialRecord.previousVersionCode != lastRecord.currentVersionCode) {
                    evt = AppInfoCookedData(
                        lastRecord.appTitle, EventType.APP_UPDATED,
                        lastRecord.currentVersionCode, lastRecord.appId, lastRecord.isSystemApp
                    )
                }
                if (initialRecord.previousVersionCode == 0L) {
                    evt = AppInfoCookedData(
                        lastRecord.appTitle, EventType.APP_INSTALLED,
                        lastRecord.currentVersionCode, lastRecord.appId, lastRecord.isSystemApp
                    )
                }
                if (evt != null) {
                    appList.add(evt)
                }
            }

            if (appList.isEmpty())
                iCookingDone.onComplete(arrayListOf())
            else {
                for (app in appList) {
                    app.packageName = db.appsDao().getPackageByID(app.appId)
                }
                @Suppress("UNCHECKED_CAST")
                iCookingDone.onComplete(appList as ArrayList<T>)
            }
        }
    }
}