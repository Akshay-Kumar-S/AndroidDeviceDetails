package com.example.androidDeviceDetails.cooker

import android.location.Geocoder
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.LocationModel
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.location.LocationData
import com.github.davidmoten.geo.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationCooker : BaseCooker() {
    private val geoHashLength = 8
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!
    private val geoCoder: Geocoder = Geocoder(DeviceDetailsApplication.instance)

    private fun cookData(locationList: ArrayList<LocationModel>): ArrayList<LocationData> {
        val geoHashList = ArrayList<String>()
        var prevLocationHash = ""
        for (loc in locationList) {
            val newHash = GeoHash.encodeHash(loc.latitude, loc.longitude, geoHashLength)
            if (newHash != prevLocationHash) {
                prevLocationHash = newHash
                geoHashList.add(newHash)
            }
        }
        val geoHashCount = geoHashList.groupingBy { it }.eachCount()
        val locationDisplayList = ArrayList<LocationData>()
        for ((geoHash, count) in geoHashCount) {
            val latLong = GeoHash.decodeHash(geoHash)
            val address = geoCoder.getFromLocation(latLong.lat, latLong.lon, 1)[0]?.locality
            locationDisplayList.add(
                LocationData(geoHash, count, address ?: "cannot locate"
                )
            )
        }
        return locationDisplayList
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        GlobalScope.launch {
            val res = locationDatabase.locationDao()
                .readDataFromDate(time.startTime, time.endTime) as ArrayList<LocationModel>
            iCookingDone.onComplete(cookData(res) as ArrayList<T>)
        }
    }

}