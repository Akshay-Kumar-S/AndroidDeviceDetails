package com.example.androidDeviceDetails.models.location

import com.example.androidDeviceDetails.adapters.LocationAdapter

/**
 * A data class used by the list view adaptor for displaying in the [LocationAdapter]
 */
data class LocationData(
    var latitude: Double, var longitude: Double, var count: Int,
    var address: String, var totalTime: Long
)