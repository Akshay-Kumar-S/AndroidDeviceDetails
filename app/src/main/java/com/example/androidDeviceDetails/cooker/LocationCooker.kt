package com.example.androidDeviceDetails.cooker

import android.location.Geocoder
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.LocationModel
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.location.LocationData
import com.example.androidDeviceDetails.utils.Utils
import com.github.davidmoten.geo.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationCooker : BaseCooker() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        GlobalScope.launch {
            val res = RoomDB.getDatabase()!!.locationDao()
                .readDataFromDate(time.startTime, time.endTime) as ArrayList<LocationModel>
            if (res.isNotEmpty()) {
                val processedData = processData(res)
                val cookedData = cookProcessedData(processedData)
                callback.onDone(cookedData as ArrayList<T>)
            } else {
                callback.onDone(arrayListOf())
            }
        }
    }

    private fun processData(locationList: ArrayList<LocationModel>): HashMap<String, LocationData> {
        val processedLocations = HashMap<String, LocationData>()
        var preLoc = locationList.first()
        val firstHash = GeoHash.encodeHash(preLoc.latitude, preLoc.longitude, Utils.GEOHASH_LENGTH)
        processedLocations[firstHash] = LocationData(preLoc.latitude, preLoc.longitude, 1, "", 0)
        for (loc in locationList.subList(1, locationList.size)) {
            val geoHash = GeoHash.encodeHash(loc.latitude, loc.longitude, Utils.GEOHASH_LENGTH)
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
            val address = Geocoder(DeviceDetailsApplication.instance).getFromLocation(
                loc.latitude, loc.longitude, 1
            ).first()
            if (address.featureName != null) loc.address += "${address.featureName}, "
            if (address.locality != null) loc.address += "${address.locality}, "
            if (address.adminArea != null) loc.address += address.adminArea
            if (loc.address == "") loc.address =
                DeviceDetailsApplication.instance.getString(R.string.Unknown_location)
        }
        return processedData.values.toMutableList()
            .filter { it.totalTime > 0 } as ArrayList<LocationData>
    }
}