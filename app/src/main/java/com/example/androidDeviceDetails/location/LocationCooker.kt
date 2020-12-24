package com.example.androidDeviceDetails.location

import com.example.androidDeviceDetails.location.models.LocationModel
import com.fonfon.kgeohash.GeoHash

class LocationCooker {
    private val geoHashLength: Int = 6

    fun cookData1(locationList: List<LocationModel>): MutableList<LocationModel> {
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

    fun cookData(locationList: List<LocationModel>): Map<String, Int> {
        val cookedLocationList = emptyList<LocationModel>().toMutableList()
        var prevLocationHash = ""
        for (location in locationList) {
            val newHash = GeoHash(location.latitude!!,location.longitude!!,geoHashLength).toString()
            if (newHash != prevLocationHash) {
                prevLocationHash = location.geoHash!!
                cookedLocationList.add(location)
            }
        }
//        return cookedLocationList
       return cookedLocationList.groupingBy { it.geoHash!! }.eachCount()
    }

    fun sortDate(countedData: Map<String, Int>,isDescending: Boolean): Map<String, Int> {
        return if (isDescending)
            countedData.toList().sortedBy { (_, value) -> value }.reversed().toMap()
        else
            countedData.toList().sortedBy { (_, value) -> value }.toMap()

    }
}