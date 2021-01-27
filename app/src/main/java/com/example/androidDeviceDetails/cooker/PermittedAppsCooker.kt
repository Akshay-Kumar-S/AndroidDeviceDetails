package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.ui.PermittedAppsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class PermittedAppsCooker(var type: String) : BaseCooker() {

    /**
     * Cook data for App Info from the collected data available in the [AppPermissionDao] database for
     * the requested time interval.
     * >
     * Overrides : [cook] in [BaseCooker]
     * @param time data class object that contains start time and end time.
     * @param callback A callback that accepts the cooked list once cooking is done
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        GlobalScope.launch(Dispatchers.IO) {
            val db = RoomDB.getDatabase()!!
            val appList = arrayListOf<PermittedAppsCookedData>()
            val ids = db.AppPermissionDao().getPermittedApps()
            var perm = ""
            for (id in ids) {
                var str = type.replace(PermittedAppsActivity.NAME,"")
                when(str){
                    "Phone" -> perm="PHONE"
                    "Call Logs" -> perm="CALL_LOG"
                    "Contacts" -> perm="CONTACTS"
                    "SMS" -> perm="SMS"
                    "Location" -> perm="LOCATION"
                    "Camera" -> perm="CAMERA"
                    "Microphone" -> perm="RECORD_AUDIO"
                    "Storage" -> perm="STORAGE"
                    "Calender" -> perm="CALENDAR"
                    "Body Sensors" -> perm="BODY_SENSORS"
                    "Physical Activity" -> perm="ACTIVITY_RECOGNITION"
                }
                if(id.allowed_permissions.contains(perm)){
                appList.add(PermittedAppsCookedData(id.package_name,id.apk_title,id.version_name,true))
                }
                if(id.denied_permissions.contains(perm)){
                    appList.add(PermittedAppsCookedData(id.package_name,id.apk_title,id.version_name,false))
                }
            }
                callback.onDone(appList as ArrayList<T>)
        }
    }
}