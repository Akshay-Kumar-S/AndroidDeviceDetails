package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.models.database.AppPermissionsRaw
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Implements [BaseCollector].
 * A time based collector which collects the allowed and denied permissions of individual apps.
 */

class PermissionCollector(var context: Context) : BaseCollector() {

    override fun collect() {
        getInstalledAppsPermission()
    }

    /**
     * Collect permissions that are allowed and denied for each app using [PackageManager.GET_PERMISSIONS]
     * store it as a List<[AppPermissionsRaw]>
     * and writes into [RoomDB.appPermissionDao].
     */
    private fun getInstalledAppsPermission() {
        val db = RoomDB.getDatabase()!!
        val appList = context.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        for (app in appList) {
            val packageName = app.packageName
            val allowed = ArrayList<String>()
            val denied = ArrayList<String>()
            try {
                for (i in app.requestedPermissions.indices) {
                    if (app.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0) {
                        allowed.add(app.requestedPermissions[i])
                    } else denied.add(app.requestedPermissions[i])
                }
            } catch (e: Exception) {
            }
            GlobalScope.launch {
                val uid = db.appsDao().getIdByName(packageName)
                val appPermissions =
                    AppPermissionsRaw(uid, Gson().toJson(allowed), Gson().toJson(denied))
                try {
                    db.appPermissionDao().insert(appPermissions)
                } catch (e: SQLiteConstraintException) {
                }
            }
        }
    }
}
