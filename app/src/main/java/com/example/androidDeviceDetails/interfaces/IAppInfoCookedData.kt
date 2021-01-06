package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData

interface IAppInfoCookedData {
    fun onDataReceived(appList: List<AppInfoCookedData>)
    fun onNoData()
}