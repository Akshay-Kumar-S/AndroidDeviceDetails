package com.example.androidDeviceDetails.utils

import android.content.Context
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.appInfoModels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Events are made into [AppInfoRaw] and [AppHistoryRaw] and inserted into
 * [AppInfoDao] and [AppHistoryDao]
 *
 */
object AppInfoCollectionHelper {
    /**
     * Updates  [AppInfoDao] and [AppHistoryDao] databases with events [EventType.APP_INSTALLED]
     * or [EventType.APP_UPDATED]
     *
     * @param context Event context
     * @param packageName Package name of the app
     */
    fun appInstalled(context: Context, packageName: String) {
        val latestAppDetails = Utils.getAppDetails(context, packageName)
        val db = RoomDB.getDatabase(context)!!
        GlobalScope.launch(Dispatchers.IO) {
            var id = db.appsDao().getIdByName(packageName)
            if (id == 0) {
                AppInfoDbHelper.writeToAppsDb(0, packageName, latestAppDetails, db)
                id = db.appsDao().getIdByName(packageName)
                AppInfoDbHelper.writeToAppHistoryDb(
                    id,
                    EventType.APP_INSTALLED.ordinal,
                    latestAppDetails,
                    db
                )
            } else {
                val currentAppHistory = db.appsDao().getById(id)
                val event =
                    if (currentAppHistory.appTitle != latestAppDetails.appTitle ||
                        currentAppHistory.currentVersionCode < latestAppDetails.versionCode
                    ) {
                        EventType.APP_UPDATED.ordinal
                    } else {
                        EventType.APP_INSTALLED.ordinal
                    }
                AppInfoDbHelper.writeToAppHistoryDb(
                    id,
                    event,
                    latestAppDetails,
                    db,
                    currentAppHistory.currentVersionCode
                )
                AppInfoDbHelper.writeToAppsDb(id, packageName, latestAppDetails, db)
            }
        }
    }

    /**
     * Updates  [AppInfoDao] and [AppHistoryDao] databases with event [EventType.APP_UNINSTALLED]
     * @param context Event context
     * @param packageName Package name of the app
     */
    fun appUninstalled(context: Context, packageName: String) {
        val db = RoomDB.getDatabase(context)!!
        GlobalScope.launch(Dispatchers.IO) {
            val id = db.appsDao().getIdByName(packageName)
            val currentAppHistory = db.appsDao().getById(id)
            val appDetails =
                AppDetails(
                    0,
                    currentAppHistory.versionName,
                    currentAppHistory.appSize,
                    currentAppHistory.appTitle,
                    currentAppHistory.isSystemApp
                )
            AppInfoDbHelper.writeToAppHistoryDb(
                id,
                EventType.APP_UNINSTALLED.ordinal,
                appDetails,
                db,
                currentAppHistory.currentVersionCode
            )
            AppInfoDbHelper.writeToAppsDb(id, packageName, appDetails, db)
        }
    }

    /**
     * Updates  [AppInfoDao] and [AppHistoryDao] databases with events [EventType.APP_INSTALLED]
     * or [EventType.APP_UPDATED]
     *
     * @param context Event context
     * @param packageName Package name of the app
     */
    fun appUpgraded(context: Context, packageName: String) {
        val db = RoomDB.getDatabase(context)!!
        GlobalScope.launch(Dispatchers.IO) {
            val latestAppDetails = Utils.getAppDetails(context, packageName)
            val id = db.appsDao().getIdByName(packageName)
            val currentAppHistory = db.appsDao().getById(id)
            if (currentAppHistory.currentVersionCode < latestAppDetails.versionCode || currentAppHistory.appTitle != latestAppDetails.appTitle) {
                AppInfoDbHelper.writeToAppHistoryDb(
                    id,
                    EventType.APP_UPDATED.ordinal,
                    latestAppDetails,
                    db,
                    currentAppHistory.currentVersionCode
                )
                AppInfoDbHelper.writeToAppsDb(id, packageName, latestAppDetails, db)
            }
        }
    }

}