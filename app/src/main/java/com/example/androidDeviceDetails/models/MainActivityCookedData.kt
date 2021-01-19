package com.example.androidDeviceDetails.models

import com.example.androidDeviceDetails.models.appInfoModels.AppInfoRaw

data class MainActivityCookedData(
    var appInfo: List<AppInfoRaw>?,
    var totalDrop: Long,
    var deviceNetworkUsage: Pair<Long, Long>?,
    var totalPlacesVisited:Int
)
