package com.example.androidDeviceDetails.location

import android.util.Log
import com.example.androidDeviceDetails.ICookingDone
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.location.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.fonfon.kgeohash.GeoHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationCooker : BaseCooker(){
    private val geoHashLength: Int = 6
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!
    private lateinit var res: List<LocationModel>
    private lateinit var countedData: Map<String, Int>
    private var prevDate: Long = 0L


//    private suspend fun getData(date: Long?): List<LocationModel> =
//        withContext(Dispatchers.Default) {
//            if (date != null) {
//                return@withContext locationDatabase.locationDao()
//                    .readDataFromDate(date, date + TimeUnit.DAYS.toMillis(1))
//            }
//            return@withContext locationDatabase.locationDao().readAll()
//        }
//
//    fun countData(cookedData: MutableList<LocationModel>): Map<String, Int> {
//        return cookedData.groupingBy { it.geoHash!! }.eachCount()
//    }

    private fun cookData(locationList: List<LocationModel>): MutableList<LocationModel> {
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
        return cookedLocationList
    }

    override fun <T> cook(onCookingDone: ICookingDone<T>, time: Long) {
        GlobalScope.launch {
            res = locationDatabase.locationDao().readAll()
            onCookingDone.onDone(cookData(res) as MutableList<T>)
            Log.d("LocationData", "loadData: $res")
        }
    }


}