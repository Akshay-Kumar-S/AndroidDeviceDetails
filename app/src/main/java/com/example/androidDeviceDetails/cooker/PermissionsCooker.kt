package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
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
            val permissionList = arrayListOf<String>()
        permissionList.addAll(listOf("Phone","Call Logs","Contacts","SMS","Location","Camera","Microphone","Storage","Calender","Body Sensors","Physical Activity"))
            callback.onDone(permissionList as ArrayList<T>)
    }
}