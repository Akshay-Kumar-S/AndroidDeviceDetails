package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.models.location.LocationDisplayModel

interface OnItemClickListener {
    fun onItemClicked(clickedItem: LocationDisplayModel)
}