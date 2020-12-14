package com.example.androidDeviceDetails.models

import com.example.androidDeviceDetails.utils.EventType

data class AppInfoCookedData(
    var appName: String,
    var eventType: EventType,
    var versionCode: Long,
    var appId: Int,
    var packageName : String = "null"
)