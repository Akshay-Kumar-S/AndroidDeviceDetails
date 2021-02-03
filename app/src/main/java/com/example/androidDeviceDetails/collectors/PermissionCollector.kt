package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.models.database.AppPermissionsInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Implements [BaseCollector].
 *
 * A time based collector which collects the allowed and denied permissions of individual apps.
 *
 */

class PermissionCollector(var context: Context) : BaseCollector() {
    /**
     * Collect permissions that are allowed and denied for each app using [PackageManager.GET_PERMISSIONS]
     * store it as a List<[AppPermissionsInfo]>
     * and writes into [RoomDB.appPermissionDao].
     *
     */

    override fun collect() {
        installedApps()
    }

    private fun installedApps() {
        val db = RoomDB.getDatabase()!!
        val appList = context.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        for (packageInfo in appList) {
            val packageName = packageInfo.packageName.toString()
            var perms = getPermissions(packageInfo)
            GlobalScope.launch {
                var uid = db.appsDao().getIdByName(packageName)
                val appPermissions =
                    AppPermissionsInfo(uid, perms[0].toString(), perms[1].toString())
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
     *
     */
    private fun getPermissions(packageInfo: PackageInfo): List<List<String>> {
        val allowed: MutableList<String> = ArrayList()
        val denied: MutableList<String> = ArrayList()
        var listOfPermissions: MutableList<List<String>> = ArrayList()
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
