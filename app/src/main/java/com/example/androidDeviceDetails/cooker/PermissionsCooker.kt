package com.example.androidDeviceDetails.cooker

import android.util.Log
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.RoomDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class PermissionsCooker : BaseCooker() {

    /**
     * Cook data for Permissions list
     *>
     * Overrides [cook] in [BaseCooker]
     * >
     * @param callback A callback that accepts the cooked list once the cooking is done.
     */

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        var distinct: List<String> = listOf()
        GlobalScope.launch(Dispatchers.IO) {
            var listOfPermissions: MutableList<String> = ArrayList()
            val permissionList = arrayListOf<String>()
            val db = RoomDB.getDatabase()!!
            var allowedPerms = db.appPermissionDao().getAllowedPermissions()
            var deniedPerms = db.appPermissionDao().getDeniedPermissions()
            for (i in allowedPerms) {
                    listOfPermissions.addAll(i.filterNot { "[]".indexOf(it) > -1 }.split(", "))
            }
            for (i in deniedPerms) {
                listOfPermissions.addAll(i.filterNot { "[]".indexOf(it) > -1 }.split(", "))
            }
            distinct = listOfPermissions.toSet().toList()
            permissionList.addAll(distinct)
            callback.onDone(permissionList as ArrayList<T>)
        }
    }
}