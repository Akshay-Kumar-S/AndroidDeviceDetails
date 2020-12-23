package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.appInfo.models.AppInfoCookedData

interface IAppInfoCookedData{
    fun onDataReceived(appList : List<AppInfoCookedData>)
    fun onNoData()
}