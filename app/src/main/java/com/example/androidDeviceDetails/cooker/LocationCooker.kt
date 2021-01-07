package com.example.androidDeviceDetails.cooker

import android.util.Log
import com.example.androidDeviceDetails.ICookingDone
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.fonfon.kgeohash.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationCooker : BaseCooker() {
    private val geoHashLength: Int = 6
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!
    private lateinit var res: ArrayList<LocationModel>

    private fun cookData(locationList: ArrayList<LocationModel>): ArrayList<LocationModel> {
        val cookedLocationList = emptyList<LocationModel>().toMutableList()
        var prevLocationHash = ""
        for (location in locationList) {
            val newHash =
                GeoHash(location.latitude!!, location.longitude!!, geoHashLength).toString()
            if (newHash != prevLocationHash) {
                prevLocationHash = location.geoHash!!
                cookedLocationList.add(location)
            }
        }
        return cookedLocationList as ArrayList<LocationModel>
    }

    override fun <T> cook(time: TimeInterval, callback: ICookingDone<T>) {
        GlobalScope.launch {
            res = locationDatabase.locationDao().readAll() as ArrayList<LocationModel>
            callback.onDone(cookData(res) as ArrayList<T>)
            Log.d("LocationData", "loadData: $res")
        }
    }

}