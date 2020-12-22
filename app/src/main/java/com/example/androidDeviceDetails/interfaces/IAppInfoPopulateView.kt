package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.models.AppInfoCookedData

interface IAppInfoPopulateView {
    fun populateView(filteredList: MutableList<AppInfoCookedData>)
}