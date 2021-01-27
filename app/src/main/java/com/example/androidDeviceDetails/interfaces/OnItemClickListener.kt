package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.models.location.LocationItemViewHolder

interface OnItemClickListener {
    fun onItemClicked(clickedItem: LocationItemViewHolder)
}