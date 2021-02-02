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
    private fun cookData(locationList: ArrayList<LocationModel>): ArrayList<LocationData> {
        val geoHashList = ArrayList<String>()
        var prevLocationHash = ""
        for (loc in locationList) {
            val newHash = GeoHash.encodeHash(loc.latitude, loc.longitude, 8)
            if (newHash != prevLocationHash) {
                prevLocationHash = newHash
                geoHashList.add(newHash)
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
        return processedData.values.toMutableList() as ArrayList<LocationData>
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