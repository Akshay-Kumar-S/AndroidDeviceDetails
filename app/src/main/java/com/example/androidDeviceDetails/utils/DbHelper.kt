package com.example.androidDeviceDetails.utils

import com.example.androidDeviceDetails.models.AppDetails
import com.example.androidDeviceDetails.models.AppHistory
import com.example.androidDeviceDetails.models.Apps
import com.example.androidDeviceDetails.models.RoomDB

object DbHelper {
    fun writeToAppsDb(id: Int, packageName: String, db: RoomDB) {
        db.appsDao().insertAll(
            Apps(
                uid = id,
                packageName = packageName,
            )
        )
    }

    fun writeToAppHistoryDb(
        id: Int,
        eventType: Int,
        appDetails: AppDetails,
        db: RoomDB,
        timestamp:Long=System.currentTimeMillis()
    ) {
        db.appHistoryDao().insertAll(
            AppHistory(
                rowId = 0,
                appId = id,
                timestamp =timestamp ,
                eventType = eventType,
                versionCode = appDetails.versionCode,
                versionName = appDetails.versionName,
                appSize = appDetails.appSize,
                appTitle = appDetails.appTitle
            )
        )
    }
}