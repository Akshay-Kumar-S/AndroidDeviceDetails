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

class LocationCooker : BaseCooker() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        GlobalScope.launch {
            val res = RoomDB.getDatabase()!!.locationDao()
                .readDataFromDate(time.startTime, time.endTime) as ArrayList<LocationModel>
            if (res.isNotEmpty()) {
                val processedData = processData(res)
                val cookedData = cookProcessedData(processedData)
                iCookingDone.onComplete(cookedData as ArrayList<T>)
            } else {
                iCookingDone.onComplete(arrayListOf())
            }
        }
    }

    private fun processData(locationList: ArrayList<LocationModel>): HashMap<String, LocationData> {
        val processedLocations = HashMap<String, LocationData>()
        var preLoc = locationList.first()
        val firstHash =
            GeoHash.encodeHash(preLoc.latitude, preLoc.longitude, LocationConstants.GEO_HASH_LENGTH)
        processedLocations[firstHash] = LocationData(preLoc.latitude, preLoc.longitude, 1, "", 0)
        for (loc in locationList.subList(1, locationList.size)) {
            val geoHash =
                GeoHash.encodeHash(loc.latitude, loc.longitude, LocationConstants.GEO_HASH_LENGTH)
            if (loc.latitude != preLoc.latitude && loc.longitude != preLoc.longitude) {
                if (geoHash in processedLocations.keys) {
                    processedLocations[geoHash]!!.latitude += loc.latitude
                    processedLocations[geoHash]!!.longitude += loc.longitude
                    processedLocations[geoHash]!!.count += 1
                    processedLocations[geoHash]!!.totalTime += loc.timeStamp - preLoc.timeStamp
                } else {
                    processedLocations[geoHash] = LocationData(
                        loc.latitude, loc.longitude, 1, "", loc.timeStamp - preLoc.timeStamp
                    )
                }
            } else {
                processedLocations[geoHash]!!.totalTime += loc.timeStamp - preLoc.timeStamp
            }
            preLoc = loc
        }
        return processedLocations
    }

    private fun cookProcessedData(processedData: HashMap<String, LocationData>): ArrayList<LocationData> {
        processedData.forEach { (_, loc) ->
            loc.latitude /= loc.count
            loc.longitude /= loc.count
            loc.address= getAddress(loc.latitude, loc.longitude)
        }
        return processedData.values.toMutableList()
            .filter { it.totalTime > 0 } as ArrayList<LocationData>
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val address = Geocoder(DeviceDetailsApplication.instance).getFromLocation(
            latitude, longitude, 1)
        var formattedAddress =""
        return if (address.isNotEmpty()) {
            val firstAddress = address.first()
            if (!firstAddress.featureName.isNullOrEmpty()) formattedAddress += "${firstAddress.featureName}, "
            if (!firstAddress.locality.isNullOrEmpty()) formattedAddress += "${firstAddress.locality}, "
            if (!firstAddress.adminArea.isNullOrBlank()) formattedAddress += firstAddress.adminArea
            if (formattedAddress.isBlank()) formattedAddress =
                DeviceDetailsApplication.instance.getString(R.string.Unknown_location)
            formattedAddress
        } else
            DeviceDetailsApplication.instance.getString(R.string.Unknown_location)
    }
}