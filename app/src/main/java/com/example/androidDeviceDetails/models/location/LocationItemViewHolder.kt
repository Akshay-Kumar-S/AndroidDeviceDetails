package com.example.androidDeviceDetails.models.location

import com.example.androidDeviceDetails.adapters.LocationAdapter

/**
 * A data class used by the list view adaptor for displaying in the [LocationAdapter]
 */
data class LocationItemViewHolder(
    val geoHash: String,
    val count: Int,
    val address: String

)