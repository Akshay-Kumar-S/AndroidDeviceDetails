package com.example.androidDeviceDetails.cooker

import android.location.Geocoder
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.database.LocationModel
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.location.LocationConstants
import com.example.androidDeviceDetails.models.location.LocationData
import com.github.davidmoten.geo.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LocationCooker : BaseCooker() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        GlobalScope.launch {
            val res = RoomDB.getDatabase()!!.locationDao()
                .readDataFromDate(time.startTime, time.endTime) as ArrayList<LocationModel>
            if (res.isNotEmpty())
                iCookingDone.onComplete(cookData(res) as ArrayList<T>)
            else
                iCookingDone.onComplete(arrayListOf())
        }
    }

    private fun cookData(locationList: ArrayList<LocationModel>): ArrayList<LocationData> {
        val geoHashMap = HashMap<String, LocationData>()
        val countMap = HashMap<String, Int>()
        var preLoc = locationList.first()
        var prevHash = getGeoHash(preLoc.latitude, preLoc.longitude)
        geoHashMap[prevHash] = LocationData(preLoc.latitude, preLoc.longitude, 1, "", 0)
        countMap[prevHash] = 1
        for (loc in locationList.subList(1, locationList.size)) {
            val newHash = getGeoHash(loc.latitude, loc.longitude)
            if (newHash in geoHashMap.keys) {
                countMap[newHash] = countMap[newHash]!! + 1
                geoHashMap[newHash]?.run {
                    latitude += loc.latitude
                    longitude += loc.longitude
                    totalTime += loc.timeStamp - preLoc.timeStamp
                }
                if (prevHash != newHash)
                    geoHashMap[newHash]!!.count += 1
            } else {
                geoHashMap[newHash] = LocationData(
                    loc.latitude, loc.longitude, 1, "", loc.timeStamp - preLoc.timeStamp
                )
                countMap[newHash] = 1
            }
            prevHash = newHash
            preLoc = loc
        }
        return cookProcessedData(geoHashMap, countMap)
    }

    private fun cookProcessedData(
        geoHashMap: HashMap<String, LocationData>, countMap: HashMap<String, Int>
    ): ArrayList<LocationData> {
        geoHashMap.forEach { (geoHash, loc) ->
            loc.latitude /= countMap[geoHash]!!
            loc.longitude /= countMap[geoHash]!!
            loc.address = getAddress(loc.latitude, loc.longitude)
        }
        return geoHashMap.values.toMutableList() as ArrayList<LocationData>
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val address =
            Geocoder(DeviceDetailsApplication.instance).getFromLocation(latitude, longitude, 1)
        var formattedAddress = ""
        return if (address.isNotEmpty()) {
            val firstAddress = address.first()
            if (!firstAddress.featureName.isNullOrBlank()) formattedAddress += "${firstAddress.featureName}, "
            if (!firstAddress.locality.isNullOrEmpty()) formattedAddress += "${firstAddress.locality}, "
            if (!firstAddress.adminArea.isNullOrBlank()) formattedAddress += firstAddress.adminArea
            if (formattedAddress.isBlank()) formattedAddress =
                DeviceDetailsApplication.instance.getString(R.string.Unknown_location)
            formattedAddress
        } else
            DeviceDetailsApplication.instance.getString(R.string.Unknown_location)
    }

    private fun getGeoHash(latitude: Double, longitude: Double): String =
        GeoHash.encodeHash(latitude, longitude, LocationConstants.GEO_HASH_LENGTH)
}