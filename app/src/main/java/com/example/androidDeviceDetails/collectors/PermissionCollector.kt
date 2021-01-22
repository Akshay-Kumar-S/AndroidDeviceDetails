package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.database.AppPermissionsInfo

import com.example.androidDeviceDetails.models.database.RoomDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PermissionCollector(var context: Context) : BaseCollector() {
    val db=RoomDB.getDatabase()!!
    fun installedApps() {
        val list = context.packageManager.getInstalledPackages(0)
        for (i in list.indices) {
            val packageInfo = list[i]

            if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
                val packageName = packageInfo.packageName.toString()
                Log.e("perms $packageName", getGrantedPermissions(packageName).toString())
                var perms = getGrantedPermissions(packageName).toString()
                GlobalScope.launch {
                    var uid = db.appsDao()?.getIdByName(packageName)
                    val appPermissions =  AppPermissionsInfo( uid, perms)
                    db.AppPermissionDao()?.insert(appPermissions)
                }
            }
        }
    }

    override fun start() {
        //installedApps()
    }

    fun getGrantedPermissions(appPackage: String?): List<String> {
        val granted: MutableList<String> = ArrayList()
        try {
            val pi = context.packageManager.getPackageInfo(appPackage!!, PackageManager.GET_PERMISSIONS)
            for (i in pi.requestedPermissions.indices) {
                if (pi.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0) {
                    granted.add(pi.requestedPermissions[i])
                }
            }
        } catch (e: Exception) {
        }
        return granted
    }
}
