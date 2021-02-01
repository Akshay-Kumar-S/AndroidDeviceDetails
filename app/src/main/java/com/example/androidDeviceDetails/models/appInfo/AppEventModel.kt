package com.example.androidDeviceDetails.models.appInfo

import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.androidDeviceDetails.database.AppInfoDao

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
data class AppInfoCookedData(
    var appName: String,
    var eventType: EventType,
    var versionCode: Long,
    var appId: Int,
    var isSystemApp: Boolean,
    var packageName: String = "null",
    var size: Long = 0
)

data class AppInfoItemViewHolder(
    var appNameView: TextView,
    var versionCodeTextView: TextView,
    var eventTypeTextView: TextView,
    var appIconView: ImageView,
    var eventBadge: ImageView,
    var uninstallButton: ImageButton
)

data class DonutChartData(
    val value1: Int,
    val value2: Int,
    val value3: Int,
    val value4: Int

)

data class ProgressbarViewHolder(
    val updated_progressBar: ProgressBar,
    val enroll_progressbar: ProgressBar,
    val installed_progressBar: ProgressBar,
    val uninstalled_progressbar: ProgressBar,
    val enroll_count: TextView,
    val install_count: TextView,
    val update_count: TextView,
    val uninstall_count: TextView
)