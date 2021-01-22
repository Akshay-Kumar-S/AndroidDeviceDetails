package com.example.androidDeviceDetails.models.permissionsModel

import com.example.androidDeviceDetails.models.appInfo.EventType

/**
 * A data class used to populate AppInfo UI
 *
 * @param appName Name of the app
 * @param eventType Type of the event in [EventType]
 * @param versionCode Version code of the app
 * @param appId of the app from [AppInfoDao]
 * @param isSystemApp Whether the app is a system app or not
 * @param packageName Package name of the app
 */
data class PermittedAppsCookedData(
    var appName: String,
    var eventType: EventType,
    var versionCode: Long,
    var appId: Int,
    var isSystemApp: Boolean,
    var packageName: String = "null"
)