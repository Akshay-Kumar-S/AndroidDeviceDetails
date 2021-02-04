package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.models.database.AppPermissionsRaw
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
            val perms = getPermissions(app)
            GlobalScope.launch {
                val uid = db.appsDao().getIdByName(packageName)
                val appPermissions =
                    AppPermissionsRaw(uid, perms[0].toString(), perms[1].toString())
                try {
                    db.appPermissionDao().insert(appPermissions)
                } catch (e: SQLiteConstraintException) {
                }
            }
        }
    }

    /**
     * Collects the allowed and denied permission list for each app
     * store it as a List<List<String>>
     * and returns it to installedApps().
     */
    private fun getPermissions(packageInfo: PackageInfo): List<List<String>> {
        val allowed = ArrayList<String>()
        val denied = ArrayList<String>()
        val listOfPermissions = ArrayList<List<String>>()
        try {
            for (i in packageInfo.requestedPermissions.indices) {
                if (packageInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0) {
                    allowed.add(packageInfo.requestedPermissions[i])
                } else if (packageInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED == 0) {
                    denied.add(packageInfo.requestedPermissions[i])
                }
            }
        } catch (e: Exception) {
        }
        listOfPermissions.add(allowed)
        listOfPermissions.add(denied)
        return listOfPermissions
    }
}
