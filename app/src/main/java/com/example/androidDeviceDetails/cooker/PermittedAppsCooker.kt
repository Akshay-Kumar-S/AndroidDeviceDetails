package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.AppPermissionDao
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.ui.PermittedAppsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class PermittedAppsCooker() : BaseCooker() {

    /**
     * Cook data for Permitted apps from the collected data available in the [AppPermissionDao] database.
     * >
     * Overrides : [cook] in [BaseCooker]
     * @param callback A callback that accepts the cooked list once cooking is done
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        GlobalScope.launch(Dispatchers.IO) {
            val db = RoomDB.getDatabase()!!
            val appList = arrayListOf<PermittedAppsCookedData>()
            val ids = db.appPermissionDao().getPermittedApps()
            for (id in ids) {
                if (id.allowedPermissions.contains(PermittedAppsActivity.PERMISSION) && !id.deniedPermissions.contains(
                        PermittedAppsActivity.PERMISSION
                    )
                ) {
                    appList.add(
                        PermittedAppsCookedData(
                            id.packageName,
                            id.apkTitle,
                            id.versionName,
                            true
                        )
                    )
                }
                if (id.deniedPermissions.contains(PermittedAppsActivity.PERMISSION)) {
                    appList.add(
                        PermittedAppsCookedData(
                            id.packageName,
                            id.apkTitle,
                            id.versionName,
                            false
                        )
                    )
                }
            }
            callback.onDone(appList as ArrayList<T>)
        }
    }
}