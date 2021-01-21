package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.base.BaseCollector

class PermissionCollector(var context: Context) : BaseCollector() {

    fun installedApps() {

        val list = context.packageManager.getInstalledPackages(0)
        for (i in list.indices) {
            val packageInfo = list[i]

            if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
                val packageName = packageInfo.packageName.toString()
                Log.e("perms $packageName", getGrantedPermissions(packageName).toString())
                var perms = getGrantedPermissions(packageName).toString()
                //Log.e("Permission$i",checkPermission("READ_CONTACTS",1,0).toString())
                //Log.e("Permission$i", packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).toString())
                //Log.e("App List$i", packageName)
//                arrayAdapter = ArrayAdapter(this,
//                    R.layout.support_simple_spinner_dropdown_item, list as List<*>)
//                listView.adapter = arrayAdapter
                //packageManager.getPackageInfo(packageName,PackageManager.GET_PERMISSIONS)
            }
        }
    }

    fun getGrantedPermissions(appPackage: String?): List<String>? {
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
