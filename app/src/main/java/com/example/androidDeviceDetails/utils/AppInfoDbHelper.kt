package com.example.androidDeviceDetails.utils

import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.appInfoModels.AppDetails
import com.example.androidDeviceDetails.models.appInfoModels.AppHistoryEntity
import com.example.androidDeviceDetails.models.appInfoModels.AppsEntity

object AppInfoDbHelper {
    fun writeToAppsDb(id: Int, packageName: String, appDetails: AppDetails, db: RoomDB) {
        db.appsDao().insertAll(
            AppsEntity(
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

    fun writeToAppHistoryDb(
        id: Int,
        eventType: Int,
        appDetails: AppDetails,
        db: RoomDB,
        previousVersionCode: Long = 0,
        timestamp: Long = System.currentTimeMillis()
    ) {
        db.appHistoryDao().insertAll(
            AppHistoryEntity(
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