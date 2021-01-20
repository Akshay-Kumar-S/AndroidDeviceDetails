package com.example.androidDeviceDetails.models.appInfo

/**
 * A data class used to enter app details
 *
 * @param versionCode Version code of the app
 * @param versionName Version name of the app
 * @param appSize Size of the app in MB
 * @param appTitle Name of the app
 * @param isSystemApp Whether the app is a system app or not
 */
data class AppDetails(
    var versionCode: Long,
    var versionName: String,
    var appSize: Long,
    var appTitle: String,
    var isSystemApp: Boolean
)