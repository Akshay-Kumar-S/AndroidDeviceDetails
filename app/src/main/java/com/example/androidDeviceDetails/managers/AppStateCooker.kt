package com.example.androidDeviceDetails.managers

import android.content.Context
import com.example.androidDeviceDetails.models.CookedData
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.utils.EventType

class AppStateCooker {

    companion object {
        fun createInstance(): AppStateCooker {
            return AppStateCooker()
        }
    }

    fun getAppsBetween(startTime: Long, endTime: Long, context: Context): List<CookedData> {
        val db = RoomDB.getDatabase(context)!!
        val appList = listOf<CookedData>().toMutableList()
        val ids = db.appHistoryDao().getIdsBetween(startTime, endTime)
        for (id in ids) {
            val lastRecord = db.appHistoryDao().getLatestRecordBetween(id, startTime, endTime)
            val initialRecord = db.appHistoryDao().getInitialRecordBetween(id, startTime, endTime)
            @Suppress("CascadeIf")
            var evt : CookedData? = null
            if (lastRecord.eventType == EventType.APP_ENROLL.ordinal) {
                appList.add(CookedData(lastRecord.appTitle, EventType.APP_ENROLL))
                continue
            } else if (lastRecord.eventType == EventType.APP_UNINSTALLED.ordinal) {
                appList.add(CookedData(lastRecord.appTitle, EventType.APP_UNINSTALLED))
                continue
            } else if (initialRecord.previousVersionCode != lastRecord.currentVersionCode) {
                evt =  CookedData(lastRecord.appTitle, EventType.APP_UPDATED)
            }
            if (initialRecord.previousVersionCode == 0L) {
                evt = CookedData(lastRecord.appTitle, EventType.APP_INSTALLED)
            }
            if (evt != null) {
                appList.add(evt)
            }
        }
        return appList
    }
}