package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.interfaces.IAppInfoCookedData
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfoModels.EventType
import com.example.androidDeviceDetails.models.RoomDB

class AppInfoCooker {

    companion object {
        fun createInstance(): AppInfoCooker {
            return AppInfoCooker()
        }
    }

    fun getAppsBetween(
        startTime: Long,
        endTime: Long,
        appInfoCookedData: IAppInfoCookedData
    ) {
        val db = RoomDB.getDatabase()!!
        val appList = listOf<AppInfoCookedData>().toMutableList()
        val ids = db.appHistoryDao().getIdsBetween(startTime, endTime)
        for (id in ids) {
            val lastRecord = db.appHistoryDao().getLatestRecordBetween(id, startTime, endTime)
            val initialRecord = db.appHistoryDao().getInitialRecordBetween(id, startTime, endTime)

            @Suppress("CascadeIf")
            var evt: AppInfoCookedData? = null
            if (lastRecord.eventType == EventType.APP_ENROLL.ordinal) {
                appList.add(
                    AppInfoCookedData(
                        lastRecord.appTitle,
                        EventType.APP_ENROLL,
                        lastRecord.currentVersionCode,
                        lastRecord.appId,
                        lastRecord.isSystemApp
                    )
                )
                continue
            } else if (lastRecord.eventType == EventType.APP_UNINSTALLED.ordinal) {
                appList.add(
                    AppInfoCookedData(
                        lastRecord.appTitle,
                        EventType.APP_UNINSTALLED,
                        lastRecord.previousVersionCode,
                        lastRecord.appId,
                        lastRecord.isSystemApp
                    )
                )
                continue
            } else if (initialRecord.previousVersionCode != lastRecord.currentVersionCode) {
                evt = AppInfoCookedData(
                    lastRecord.appTitle,
                    EventType.APP_UPDATED,
                    lastRecord.currentVersionCode,
                    lastRecord.appId,
                    lastRecord.isSystemApp
                )
            }
            if (initialRecord.previousVersionCode == 0L) {
                evt = AppInfoCookedData(
                    lastRecord.appTitle,
                    EventType.APP_INSTALLED,
                    lastRecord.currentVersionCode,
                    lastRecord.appId,
                    lastRecord.isSystemApp
                )
            }
            if (evt != null) {
                appList.add(evt)
            }
        }

        if (appList.isEmpty())
            appInfoCookedData.onNoData()
        else {
            for (app in appList) {
                app.packageName = db.appsDao().getPackageByID(app.appId)
            }
            appInfoCookedData.onDataReceived(appList)
        }
    }
}