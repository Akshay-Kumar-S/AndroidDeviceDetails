package com.example.androidDeviceDetails.models

import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageEntity

data class MainActiviyCookedData(
    var appInfo:AppInfoCookedData,
    var batteryData: BatteryAppEntry,
    var deviceNetworkUsage: DeviceNetworkUsageEntity
)
