package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.permissionsModel.AppPermissionData
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppListData
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.viewModel.PermissionsViewModel
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

            for (perm in listOfPermissions) {
                val allowedAppList = ArrayList<PermittedAppListData>()
                val deniedAppList = ArrayList<PermittedAppListData>()
                for (apps in appList) {
                    if (apps.allowed_permissions.contains(perm)) {
                        allowedAppList.add(
                            PermittedAppListData(
                                apps.package_name,
                                apps.apk_title,
                                apps.version_name,
                                "",
                                ""
                            )
                        )
                    } else if (apps.denied_permissions.contains(perm)) {
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
                listOfPerm.add(AppPermissionData(perm, allowedAppList, deniedAppList))
            }
            iCookingDone.onComplete(listOfPerm as ArrayList<T>)
        }
    }

    fun getAppPermissionsAppList(
        appPermissionsViewModel: PermissionsViewModel,
        position: Int
    ): String {
        val appList = arrayListOf<PermittedAppsCookedData>()
        for (allowed in appPermissionsViewModel.permittedAppList[position].allowedAppList) {
            appList.add(
                PermittedAppsCookedData(
                    allowed.package_name,
                    allowed.apk_title,
                    allowed.version_name,
                    true
                )
            )
        }
        for (denied in appPermissionsViewModel.permittedAppList[position].deniedAppList) {
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