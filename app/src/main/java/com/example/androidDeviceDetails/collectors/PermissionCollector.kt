package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.database.AppPermissionsInfo
import com.example.androidDeviceDetails.models.database.RoomDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Implements [BaseCollector].
 *
 * A time based collector which collects the allowed and denied permissions of individual apps.
 *
 */
class PermissionCollector(var context: Context) : BaseCollector() {
    val db = RoomDB.getDatabase()!!
    private fun installedApps() {
        val list = context.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        for (packageInfo in list) {
            if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val packageName = packageInfo.packageName.toString()
                Log.e("perms $packageName", getPermissions(packageInfo).toString())
                var perms = getPermissions(packageInfo)
                GlobalScope.launch {
                    var uid = db.appsDao().getIdByName(packageName)
                    val appPermissions = AppPermissionsInfo(uid, perms[0].toString(),perms[1].toString())
                    try {
                        db.AppPermissionDao().insert(appPermissions)
                    } catch (e: SQLiteConstraintException) { }
                }
            }
        }
    }

    override fun collect() {
        installedApps()
    }

    private fun getPermissions(packageInfo: PackageInfo): List<List<String>> {
        val allowed: MutableList<String> = ArrayList()
        val denied: MutableList<String> = ArrayList()
        var listOfPermissions : MutableList<List<String>> = ArrayList()
        try {
            for (i in packageInfo.requestedPermissions.indices) {
                if (packageInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0) {
                    allowed.add(packageInfo.requestedPermissions[i])
                }
                else if (packageInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED == 0) {
                    denied.add(packageInfo.requestedPermissions[i])
                }
            }
        } catch (e: Exception) { }
        listOfPermissions.add(allowed)
        listOfPermissions.add(denied)
        return listOfPermissions
    }
}
