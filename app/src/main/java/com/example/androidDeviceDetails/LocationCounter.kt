package com.example.androidDeviceDetails

import com.example.androidDeviceDetails.models.CountModel
import com.example.androidDeviceDetails.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.fonfon.kgeohash.GeoHash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationCounter {
    private val geoHashLength: Int = 6
    private var locationDatabase = RoomDB.getDatabase()

    fun countLocation(locationList : List<LocationModel>): Map<String, Int> {
        val locationHashList= emptyList<String>().toMutableList()
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
//        return locationDatabase!!.locationDao().countHash()

    }

    suspend fun countLocation(): List<CountModel> = withContext(Dispatchers.IO) {
        return@withContext locationDatabase!!.locationDao().countHash()
    }
}