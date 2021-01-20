package com.example.androidDeviceDetails.models.battery

import com.example.androidDeviceDetails.adapters.BatteryListAdapter

/**
 * A data class used by the list view adaptor for displaying in the [BatteryListAdapter]
 */
data class BatteryAppEntry(var packageId: String, var drop: Int = 0)