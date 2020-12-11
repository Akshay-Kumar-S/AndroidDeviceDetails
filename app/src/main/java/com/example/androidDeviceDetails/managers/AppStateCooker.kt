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