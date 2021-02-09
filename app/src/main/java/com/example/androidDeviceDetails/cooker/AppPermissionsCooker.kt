package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.permissionsModel.AppPermissionData
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppListData
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class AppPermissionsCooker : BaseCooker() {

    /**
     * Cook data for Permissions list
     *>
     * Overrides [cook] in [BaseCooker]
     * >
     * @param iCookingDone A callback that accepts the cooked list once the cooking is done.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        GlobalScope.launch(Dispatchers.IO) {
            var listOfPermissions: MutableList<String> = ArrayList()
            val listOfPerm = ArrayList<AppPermissionData>()
            val db = RoomDB.getDatabase()!!
            val appList = db.appPermissionDao().getPermittedApps()
            for (apps in appList) {
                listOfPermissions.addAll(apps.allowed_permissions.filterNot { "[]".indexOf(it) > -1 }
                    .split(", "))
                listOfPermissions.addAll(apps.denied_permissions.filterNot { "[]".indexOf(it) > -1 }
                    .split(", "))
            }
            listOfPermissions = (listOfPermissions.toSet().toMutableList())

            for (permission in listOfPermissions) {
                val allowedAppList = ArrayList<PermittedAppListData>()
                val deniedAppList = ArrayList<PermittedAppListData>()
                for (apps in appList) {
                    if (apps.allowed_permissions.contains(permission)) {
                        allowedAppList.add(
                            PermittedAppListData(
                                apps.package_name,
                                apps.apk_title,
                                apps.version_name,
                                "",
                                ""
                            )
                        )
                    } else if (apps.denied_permissions.contains(permission)) {
                        deniedAppList.add(
                            PermittedAppListData(
                                apps.package_name,
                                apps.apk_title,
                                apps.version_name,
                                "",
                                ""
                            )
                        )
                    }
                }
                listOfPerm.add(AppPermissionData(permission, allowedAppList, deniedAppList))
            }
            iCookingDone.onComplete(listOfPerm as ArrayList<T>)
        }
    }

    fun getAppPermissionsAppList(appPermissionData: AppPermissionData): String {
        val appList = arrayListOf<PermittedAppsCookedData>()
        for (allowed in appPermissionData.allowedAppList) {
            appList.add(
                PermittedAppsCookedData(
                    allowed.package_name,
                    allowed.apk_title,
                    allowed.version_name,
                    true
                )
            )
        }
        for (denied in appPermissionData.deniedAppList) {
            appList.add(
                PermittedAppsCookedData(
                    denied.package_name,
                    denied.apk_title,
                    denied.version_name,
                    false
                )
            )
        }
        return Gson().toJson(appList)
    }
}