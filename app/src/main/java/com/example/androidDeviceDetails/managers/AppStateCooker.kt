package com.example.androidDeviceDetails.managers

import android.content.Context
import com.example.androidDeviceDetails.models.AppInfoCookedData
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.utils.EventType

class AppStateCooker {

    companion object {
        fun createInstance(): AppStateCooker {
            return AppStateCooker()
        }
    }

    fun getAppsBetween(startTime: Long, endTime: Long, context: Context): List<AppInfoCookedData> {
        val db = RoomDB.getDatabase(context)!!
        val appList = listOf<AppInfoCookedData>().toMutableList()
        val ids = db.appHistoryDao().getIdsBetween(startTime, endTime)
        for (id in ids) {
            val lastRecord = db.appHistoryDao().getLatestRecordBetween(id, startTime, endTime)
            val initialRecord = db.appHistoryDao().getInitialRecordBetween(id, startTime, endTime)
            @Suppress("CascadeIf")
            var evt : AppInfoCookedData? = null
            if (lastRecord.eventType == EventType.APP_ENROLL.ordinal) {
                appList.add(AppInfoCookedData(lastRecord.appTitle, EventType.APP_ENROLL,lastRecord.currentVersionCode, lastRecord.appId))
                continue
            } else if (lastRecord.eventType == EventType.APP_UNINSTALLED.ordinal) {
                appList.add(AppInfoCookedData(lastRecord.appTitle, EventType.APP_UNINSTALLED,lastRecord.previousVersionCode, lastRecord.appId))
                continue
            } else if (initialRecord.previousVersionCode != lastRecord.currentVersionCode) {
                evt =  AppInfoCookedData(lastRecord.appTitle, EventType.APP_UPDATED,lastRecord.currentVersionCode, lastRecord.appId)
            }
            if (initialRecord.previousVersionCode == 0L) {
                evt = AppInfoCookedData(lastRecord.appTitle, EventType.APP_INSTALLED,lastRecord.currentVersionCode, lastRecord.appId)
            }
            if (evt != null) {
                appList.add(evt)
            }
        }
        return appList
    }
}