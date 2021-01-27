package com.example.androidDeviceDetails.cooker

import android.location.Geocoder
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.database.RoomDB
import com.example.androidDeviceDetails.models.location.LocationItemViewHolder
import com.example.androidDeviceDetails.models.database.LocationModel
import com.github.davidmoten.geo.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationCooker : BaseCooker() {
    private val geoHashLength: Int = 8
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!
    private val geoCoder: Geocoder = Geocoder(DeviceDetailsApplication.instance)


    private fun cookData(locationList: ArrayList<LocationModel>): ArrayList<LocationItemViewHolder> {
        val cookedLocationList = emptyList<String>().toMutableList()
        var prevLocationHash = ""
        for (loc in locationList) {
            val newHash = GeoHash.encodeHash(loc.latitude, loc.longitude, geoHashLength).toString()
            if (newHash != prevLocationHash) {
                prevLocationHash = newHash
                cookedLocationList.add(newHash)
            }
        }
        val countedData = cookedLocationList.groupingBy { it }.eachCount()
        val locationItemViewHolder: ArrayList<LocationItemViewHolder> = ArrayList()
        for ((k,v) in countedData){
            val latLong = GeoHash.decodeHash(k)
            val address = geoCoder.getFromLocation(latLong.lat, latLong.lon, 1)[0]?.locality?.toString()
            locationItemViewHolder.add(LocationItemViewHolder(k,v,address ?: "cannot locate"))
        }
        return locationItemViewHolder
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        GlobalScope.launch {
            val res = locationDatabase.locationDao()
                .readDataFromDate(time.startTime, time.endTime) as ArrayList<LocationModel>
            callback.onDone(cookData(res) as ArrayList<T>)
        }
    }

}