package com.example.androidDeviceDetails.models

import com.example.androidDeviceDetails.models.appInfoModels.AppsEntity

data class MainActivityCookedData(
    var appInfo: List<AppsEntity>?,
    var totalDrop: Long,
    var deviceNetworkUsage: Pair<Long, Long>?
)
