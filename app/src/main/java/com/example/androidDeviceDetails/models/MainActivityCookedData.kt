package com.example.androidDeviceDetails.models

import com.example.androidDeviceDetails.models.database.AppInfoRaw

data class MainActivityCookedData(
    var appInfo: List<AppInfoRaw>? = null,
    var totalDrop: Long = -1,
    var deviceNetworkUsage: Pair<Long, Long>? = null,
    var totalPlacesVisited: Int = -1,
    var signalStrength: Int = Int.MIN_VALUE
)
