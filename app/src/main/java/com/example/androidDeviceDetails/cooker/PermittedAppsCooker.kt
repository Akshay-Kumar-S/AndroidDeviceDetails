package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfo.EventType
import com.example.androidDeviceDetails.models.database.AppHistoryDao
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class PermittedAppsCooker : BaseCooker() {

    /**
     * Cook data for App Info from the collected data available in the [AppHistoryDao] database for
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
            val startTime = time.startTime
            val endTime = time.endTime
            val appList = arrayListOf<PermittedAppsCookedData>()
            val ids = db.appHistoryDao().getAllApps()
            for (id in ids) {
                appList.add(PermittedAppsCookedData(id.appTitle,EventType.APP_ENROLL,id.currentVersionCode,id.appId,id.isSystemApp))
            }

            if (appList.isEmpty())
                callback.onDone(arrayListOf())
            else {
                for (app in appList) {
                    app.packageName = db.appsDao().getPackageByID(app.appId)
                }
                callback.onDone(appList as ArrayList<T>)
            }
        }
    }
}