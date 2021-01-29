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
            val ids = db.AppPermissionDao().getPermittedApps()
            var permission = ""
            for (id in ids) {
                when(PermittedAppsActivity.PERMISSION){
                    "Phone" -> permission="PHONE"
                    "Call Logs" -> permission="CALL_LOG"
                    "Contacts" -> permission="CONTACTS"
                    "SMS" -> permission="SMS"
                    "Location" -> permission="LOCATION"
                    "Camera" -> permission="CAMERA"
                    "Microphone" -> permission="RECORD_AUDIO"
                    "Storage" -> permission="STORAGE"
                    "Calender" -> permission="CALENDAR"
                    "Body Sensors" -> permission="BODY_SENSORS"
                    "Physical Activity" -> permission="ACTIVITY_RECOGNITION"
                }
                if(id.allowed_permissions.contains(permission) && !id.denied_permissions.contains(permission)){
                appList.add(PermittedAppsCookedData(id.package_name,id.apk_title,id.version_name,true))
                }
                if(id.denied_permissions.contains(permission)){
                    appList.add(PermittedAppsCookedData(id.package_name,id.apk_title,id.version_name,false))
                }
            }
                callback.onDone(appList as ArrayList<T>)
        }
    }
}