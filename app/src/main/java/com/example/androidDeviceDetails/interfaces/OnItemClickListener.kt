package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.models.location.LocationDisplayModel
import com.example.androidDeviceDetails.models.location.LocationItemViewHolder

interface OnItemClickListener {
    fun onItemClicked(clickedItem: LocationDisplayModel)
}