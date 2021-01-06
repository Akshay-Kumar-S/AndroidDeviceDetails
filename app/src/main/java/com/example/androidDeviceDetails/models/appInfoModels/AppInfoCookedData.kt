package com.example.androidDeviceDetails.models.appInfoModels

data class AppInfoCookedData(
    var appName: String,
    var eventType: EventType,
    var versionCode: Long,
    var appId: Int,
    var isSystemApp: Boolean,
    var packageName: String = "null"
)