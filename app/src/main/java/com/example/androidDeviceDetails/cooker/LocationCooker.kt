package com.example.androidDeviceDetails.cooker

import android.location.Geocoder
import android.util.Log
import com.example.androidDeviceDetails.DeviceDetailsApplication
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
    private fun processData(locationList: ArrayList<LocationModel>): HashMap<String, LocationData> {
        val processedLocations = HashMap<String, LocationData>()
        var preLoc = locationList.first()
        val firstHash = GeoHash.encodeHash(preLoc.latitude, preLoc.longitude, Utils.GEOHASH_LENGTH)
        processedLocations[firstHash] = LocationData(preLoc.latitude, preLoc.longitude, 1, "", 0)
        for (loc in locationList.subList(1, locationList.size)) {
            val geoHash = GeoHash.encodeHash(loc.latitude, loc.longitude, Utils.GEOHASH_LENGTH)
            if (loc.latitude != preLoc.latitude && loc.longitude != preLoc.longitude) {
                if (geoHash in processedLocations.keys) {
                    processedLocations[geoHash]!!.avgLatitude += loc.latitude
                    processedLocations[geoHash]!!.avgLongitude += loc.longitude
                    processedLocations[geoHash]!!.count += 1
                    processedLocations[geoHash]!!.totalTime += loc.timeStamp - preLoc.timeStamp
                } else {
                    processedLocations[geoHash] =
                        LocationData(loc.latitude, loc.longitude, 1, "", loc.timeStamp - preLoc.timeStamp)
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
            loc.avgLatitude /= loc.count
            loc.avgLongitude /= loc.count
            val address = Geocoder(DeviceDetailsApplication.instance).getFromLocation(
                loc.avgLatitude, loc.avgLongitude, 1
            ).first()
            loc.address = "${address.thoroughfare}, ${address.locality}"
        }
        return processedData.values.toMutableList().filter { it.totalTime>0 } as ArrayList<LocationData>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, iCookingDone: ICookingDone<T>) {
        GlobalScope.launch {
            val res = RoomDB.getDatabase()!!.locationDao()
                .readDataFromDate(time.startTime, time.endTime) as ArrayList<LocationModel>
            if (res.isNotEmpty()) {
                val processedData = processData(res)
                val cookedData = cookProcessedData(processedData)
                Log.d("Location", "cookedData:$cookedData ")
                iCookingDone.onComplete(cookedData as ArrayList<T>)
            } else {
                iCookingDone.onComplete(arrayListOf())
            }
        }
    }
}