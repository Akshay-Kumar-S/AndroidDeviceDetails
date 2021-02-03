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
            var allPerms = db.appPermissionDao().getAllPermissions()
            for (i in allPerms) {
                var filtered = "[]"
                var new = i.filterNot { filtered.indexOf(it) > -1 }
                listOfPermissions.addAll(new.split(","))
            }
            distinct = listOfPermissions.toSet().toList()
            Log.e("hihe", distinct.toString())
            permissionList.addAll(distinct)
            callback.onDone(permissionList as ArrayList<T>)
        }
    }
}