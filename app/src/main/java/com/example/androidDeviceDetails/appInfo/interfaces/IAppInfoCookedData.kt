package com.example.androidDeviceDetails.appInfo.interfaces

import com.example.androidDeviceDetails.appInfo.models.AppInfoCookedData

interface IAppInfoCookedData{
    fun onDataReceived(appList : List<AppInfoCookedData>, eventFilter : Int)
    fun onNoData()
}