package com.example.androidDeviceDetails

import com.example.androidDeviceDetails.models.LocationModel
import com.fonfon.kgeohash.GeoHash

class LocationCounter {
    private val geoHashLength: Int = 6

    fun countLocation(locationList: List<LocationModel>): Map<String, Int> {
        val locationHashList = emptyList<String>().toMutableList()
        var prevLocationHash = ""
        for (location in locationList) {
            val newLocationHash = GeoHash(
                location.latitude!!.toDouble(),
                location.longitude!!.toDouble(),
                geoHashLength
            ).toString()
            if (prevLocationHash != newLocationHash) {
                prevLocationHash = newLocationHash
                locationHashList.add(newLocationHash)
            }
        }
        return locationHashList.groupingBy { it }.eachCount()
    }
}