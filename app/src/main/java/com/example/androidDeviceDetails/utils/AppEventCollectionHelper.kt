package com.example.androidDeviceDetails.utils

import android.content.Context
import com.example.androidDeviceDetails.database.*
import com.example.androidDeviceDetails.models.appInfo.AppDetails
import com.example.androidDeviceDetails.models.appInfo.EventType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Events are made into [AppInfoRaw] and [AppHistoryRaw] and inserted into
 * [AppInfoDao] and [AppHistoryDao]
 *
 */
object AppEventCollectionHelper {

    val db = RoomDB.getDatabase()!!

    /**
     * Updates  [AppInfoDao] and [AppHistoryDao] databases with events [EventType.INSTALLED]
     * or [EventType.UPDATED]
     *
     * @param context Event context
     * @param packageName Package name of the app
     */
    fun appInstalled(context: Context, packageName: String) {
        val latestAppDetails = Utils.getAppDetails(context, packageName)
        GlobalScope.launch(Dispatchers.IO) {
            var id = db.appsDao().getIdByName(packageName)
            if (id == 0) {
                writeToAppsDb(0, packageName, latestAppDetails, db)
                id = db.appsDao().getIdByName(packageName)
                writeToAppHistoryDb(id, EventType.INSTALLED.ordinal, latestAppDetails, db)
            } else {
                val currentAppHistory = db.appsDao().getById(id)
                val event =
                    if (currentAppHistory.appTitle != latestAppDetails.appTitle ||
                        currentAppHistory.currentVersionCode < latestAppDetails.versionCode
                    ) {
                        EventType.UPDATED.ordinal
                    } else {
                        EventType.INSTALLED.ordinal
                    }
                writeToAppHistoryDb(
                    id, event, latestAppDetails, db, currentAppHistory.currentVersionCode
                )
                writeToAppsDb(id, packageName, latestAppDetails, db)
            }
        }
    }

    /**
     * Updates  [AppInfoDao] and [AppHistoryDao] databases with event [EventType.UNINSTALLED]
     * @param packageName Package name of the app
     */
    fun appUninstalled(packageName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val id = db.appsDao().getIdByName(packageName)
            val appHistory = db.appsDao().getById(id)
            val appDetails = AppDetails(
                0, appHistory.versionName, appHistory.appSize,
                appHistory.appTitle, appHistory.isSystemApp
            )
            writeToAppHistoryDb(
                id, EventType.UNINSTALLED.ordinal, appDetails, db, appHistory.currentVersionCode
            )
            writeToAppsDb(id, packageName, appDetails, db)
        }
    }

    /**
     * Updates  [AppInfoDao] and [AppHistoryDao] databases with events [EventType.INSTALLED]
     * or [EventType.UPDATED]
     *
     * @param context Event context
     * @param packageName Package name of the app
     */
    fun appUpgraded(context: Context, packageName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val latestAppDetails = Utils.getAppDetails(context, packageName)
            val id = db.appsDao().getIdByName(packageName)
            val appHistory = db.appsDao().getById(id)
            if (appHistory.currentVersionCode < latestAppDetails.versionCode || appHistory.appTitle != latestAppDetails.appTitle) {
                writeToAppHistoryDb(
                    id, EventType.UPDATED.ordinal, latestAppDetails,
                    db, appHistory.currentVersionCode
                )
                writeToAppsDb(id, packageName, latestAppDetails, db)
            }
        }
    }

    /**
     * Writes the given data as [AppInfoRaw] into [AppInfoDao]
     */
    fun writeToAppsDb(id: Int, packageName: String, appDetails: AppDetails, db: RoomDB) {
        db.appsDao().insertAll(
            AppInfoRaw(
                uid = id, packageName = packageName, currentVersionCode = appDetails.versionCode,
                versionName = appDetails.versionName, appSize = appDetails.appSize,
                appTitle = appDetails.appTitle, isSystemApp = appDetails.isSystemApp
            )
        )
    }

    /**
     * Writes the given data as [AppHistoryRaw] into [AppHistoryDao]
     */
    fun writeToAppHistoryDb(
        id: Int, eventType: Int, appDetails: AppDetails, db: RoomDB, previousVersionCode: Long = 0,
        timestamp: Long = System.currentTimeMillis()
    ) {
        db.appHistoryDao().insertAll(
            AppHistoryRaw(
                rowId = 0, appId = id, timestamp = timestamp, eventType = eventType,
                previousVersionCode = previousVersionCode, versionName = appDetails.versionName,
                currentVersionCode = appDetails.versionCode, appSize = appDetails.appSize,
                appTitle = appDetails.appTitle, isSystemApp = appDetails.isSystemApp
            )
        )
    }
}