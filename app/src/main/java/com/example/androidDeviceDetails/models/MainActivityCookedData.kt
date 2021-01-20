package com.example.androidDeviceDetails.models

import com.example.androidDeviceDetails.models.database.AppInfoRaw

data class MainActivityCookedData(
    var appInfo: List<AppInfoRaw>?,
    var totalDrop: Long,
    var deviceNetworkUsage: Pair<Long, Long>?,
    var totalPlacesVisited: Int,
    var signalStrength: Int
)
