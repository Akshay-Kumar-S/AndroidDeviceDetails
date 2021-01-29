package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.models.location.LocationData

interface OnItemClickListener {
    fun onItemClicked(clickedItem: LocationData)
}