package com.example.androidDeviceDetails.location

import com.example.androidDeviceDetails.location.models.LocationModel

class LocationCooker {
    private val geoHashLength: Int = 6

    fun cookData(locationList: List<LocationModel>): MutableList<LocationModel> {
        val cookedLocationList = emptyList<LocationModel>().toMutableList()
        var prevLocationHash = ""
        for (location in locationList) {
            if (location.geoHash != prevLocationHash) {
                prevLocationHash = location.geoHash!!
                cookedLocationList.add(location)
            }
        }
        return cookedLocationList
    }

    fun countData(cookedData: MutableList<LocationModel>): Map<String, Int> {
        return cookedData.groupingBy { it.geoHash!! }.eachCount()
    }
}