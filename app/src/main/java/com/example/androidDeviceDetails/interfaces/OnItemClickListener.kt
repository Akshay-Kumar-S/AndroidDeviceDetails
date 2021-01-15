package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.models.locationModels.CountModel

interface OnItemClickListener {
    fun onItemClicked(locationModel: CountModel)
}