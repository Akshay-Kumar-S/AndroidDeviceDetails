package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.models.locationModels.LocationDisplayModel

interface OnItemClickListener {
    fun onItemClicked(clickedItem: LocationDisplayModel)
}