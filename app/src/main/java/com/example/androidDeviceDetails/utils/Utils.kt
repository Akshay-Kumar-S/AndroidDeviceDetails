package com.example.androidDeviceDetails.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.example.androidDeviceDetails.models.AppDetails
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.services.CollectorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    private const val format = "dd/MM/yyyy HH:mm:ss:"
    private val formatter = SimpleDateFormat(format, Locale.ENGLISH)

    fun getDateTime(timestamp: Long): String {
        return formatter.format(Date(timestamp))
    }

    fun getEventType(eventType: Int): String {
        when (eventType) {
            1 -> return "MOVE_TO_FOREGROUND"
            2 -> return "MOVE_TO_BACKGROUND"
            3 -> return "END_OF_DAY"
            4 -> return "CONTINUE_PREVIOUS_DAY"
            5 -> return "CONFIGURATION_CHANGE"
            6 -> return "SYSTEM_INTERACTION"
            7 -> return "USER_INTERACTION"
            8 -> return "SHORTCUT_INVOCATION"
            9 -> return "CHOOSER_ACTION"
            10 -> return "NOTIFICATION_SEEN"
            11 -> return "STANDBY_BUCKET_CHANGED"
            12 -> return "NOTIFICATION_INTERRUPTION"
            13 -> return "SLICE_PINNED_PRIV"
            14 -> return "SLICE_PINNED"
            15 -> return "SCREEN_INTERACTIVE"
            16 -> return "SCREEN_NON_INTERACTIVE"
            17 -> return "KEYGUARD_SHOWN"
            18 -> return "KEYGUARD_HIDDEN"
            19 -> return "FOREGROUND_SERVICE_START"
            20 -> return "FOREGROUND_SERVICE_STOP"
            21 -> return "CONTINUING_FOREGROUND_SERVICE"
            22 -> return "ROLLOVER_FOREGROUND_SERVICE"
            23 -> return "ACTIVITY_STOPPED"
            24 -> return "ACTIVITY_DESTROYED"
            25 -> return "FLUSH_TO_DISK"
            26 -> return "DEVICE_SHUTDOWN"
            27 -> return "DEVICE_STARTUP"
        }
        return "UNDEFINED"
    }

    fun isMyServiceRunning(serviceClass: Class<CollectorService>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        for (service in manager.getRunningServices(Int.MAX_VALUE))
            if (serviceClass.name == service.service.className)
                return true
        return false
    }

    fun getAppDetails(context: Context, packageName: String): AppDetails {
        val appDetails = AppDetails(-1, "Null", -1, "Not Found")
        try {
            val pInfo2 = context.packageManager.getApplicationInfo(packageName, 0)
            val pInfo = context.packageManager.getPackageInfo(packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                appDetails.versionCode = pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                appDetails.versionCode = pInfo.versionCode.toLong()
            }
            appDetails.versionName = pInfo.versionName
            val file = File(pInfo2.sourceDir)
            appDetails.appSize = file.length() / 1024
            appDetails.appTitle = context.packageManager.getApplicationLabel(pInfo2).toString()
            Log.d("AppName", "getVersion: " + appDetails.appTitle)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appDetails
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun addInitialData(context: Context) {
        val db = RoomDB.getDatabase(context)!!
        val packages = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        GlobalScope.launch(Dispatchers.IO) {
            for (i in packages) {
                val details = getAppDetails(context, i.packageName)
                DbHelper.writeToAppsDb(0, i.packageName, details, db)
                val id = db.appsDao().getIdByName(i.packageName)
                DbHelper.writeToAppHistoryDb(
                    id,
                    EventType.APP_ENROLL.ordinal,
                    details,
                    db
                )
            }
        }
    }

}