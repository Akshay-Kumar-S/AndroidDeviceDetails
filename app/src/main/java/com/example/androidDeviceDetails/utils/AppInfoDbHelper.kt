package com.example.androidDeviceDetails.utils

import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.appInfoModels.*

object AppInfoDbHelper {
    /**
     * Writes the given data as [AppInfoRaw] into [AppInfoDao]
     */
    fun writeToAppsDb(id: Int, packageName: String, appDetails: AppDetails, db: RoomDB) {
        db.appsDao().insertAll(
            AppInfoRaw(
                uid = id,
                packageName = packageName,
                currentVersionCode = appDetails.versionCode,
                versionName = appDetails.versionName,
                appSize = appDetails.appSize,
                appTitle = appDetails.appTitle,
                isSystemApp = appDetails.isSystemApp
            )
        )
    }

    /**
     * Writes the given data as [AppHistoryRaw] into [AppHistoryDao]
     */
    fun writeToAppHistoryDb(
        id: Int,
        eventType: Int,
        appDetails: AppDetails,
        db: RoomDB,
        previousVersionCode: Long = 0,
        timestamp: Long = System.currentTimeMillis()
    ) {
        db.appHistoryDao().insertAll(
            AppHistoryRaw(
                rowId = 0,
                appId = id,
                timestamp = timestamp,
                eventType = eventType,
                previousVersionCode = previousVersionCode,
                currentVersionCode = appDetails.versionCode,
                versionName = appDetails.versionName,
                appSize = appDetails.appSize,
                appTitle = appDetails.appTitle,
                isSystemApp = appDetails.isSystemApp
            )
        )
    }
}