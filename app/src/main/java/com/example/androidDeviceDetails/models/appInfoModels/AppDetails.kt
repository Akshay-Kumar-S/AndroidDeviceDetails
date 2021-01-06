package com.example.androidDeviceDetails.models.appInfoModels

data class AppDetails(
    var versionCode: Long,
    var versionName: String,
    var appSize: Long,
    var appTitle: String,
    var isSystemApp: Boolean
)