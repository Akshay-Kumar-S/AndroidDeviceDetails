package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppListData
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.viewModel.PermissionsViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

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
            val db = RoomDB.getDatabase()!!
            val appList = db.appPermissionDao().getPermittedApps()
            for (apps in appList) {
                listOfPermissions.addAll(apps.allowed_permissions.filterNot { "[]".indexOf(it) > -1 }
                    .split(", "))
                listOfPermissions.addAll(apps.denied_permissions.filterNot { "[]".indexOf(it) > -1 }
                    .split(", "))
            }
            listOfPermissions = (listOfPermissions.toSet().toMutableList())
            val cookedPair: MutableList<Pair<List<String>, List<PermittedAppListData>>> = ArrayList()
            cookedPair.add(Pair(listOfPermissions, appList))
            iCookingDone.onComplete(cookedPair as ArrayList<T>)
        }
    }

    fun getAppPermissionsAppList(
        appPermissionsViewModel: PermissionsViewModel,
        permission: String
    ): String {
        var appList = arrayListOf<PermittedAppsCookedData>()
        for (app in appPermissionsViewModel.permittedAppList) {
            if (app.allowed_permissions.contains(permission) && !app.denied_permissions.contains(
                    permission
                )
            ) {
                appList.add(
                    PermittedAppsCookedData(
                        app.package_name,
                        app.apk_title,
                        app.version_name,
                        true
                    )
                )
            }
            if (app.denied_permissions.contains(permission)) {
                appList.add(
                    PermittedAppsCookedData(
                        app.package_name,
                        app.apk_title,
                        app.version_name,
                        false
                    )
                )
            }
        }
        return Gson().toJson(appList)
    }
}