package com.example.androidDeviceDetails.utils

import android.content.Context
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.appInfoModels.AppDetails
import com.example.androidDeviceDetails.models.appInfoModels.EventType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object AppInfoCollectionHelper {

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