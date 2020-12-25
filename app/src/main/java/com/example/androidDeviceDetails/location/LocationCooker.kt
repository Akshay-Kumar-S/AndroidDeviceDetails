package com.example.androidDeviceDetails.location

import android.util.Log
import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.location.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.fonfon.kgeohash.GeoHash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class LocationCooker : BaseCooker(){
    private val geoHashLength: Int = 6
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!
    private lateinit var res: List<LocationModel>
    private lateinit var countedData: Map<String, Int>
    private var prevDate: Long = 0L


    private suspend fun getData(date: Long?): List<LocationModel> =
        withContext(Dispatchers.Default) {
            if (date != null) {
                return@withContext locationDatabase.locationDao()
                    .readDataFromDate(date, date + TimeUnit.DAYS.toMillis(1))
            }
            return@withContext locationDatabase.locationDao().readAll()
        }

    fun loadData(date: Long? = null) =
    GlobalScope.launch {
        res = getData(date)
        Log.d("LocationData", "loadData: $res")
        if (res.isNotEmpty()) {
            locationViewModel.setDate(formatter.format(calendar.time))
            //cookedData = locationCooker.cookData(res)
            countedData = locationCooker.cookData(res)
            refreshData()
        } else {
            locationViewModel.toast("No Data on Selected Date ${formatter.format(calendar.time)}")
            calendar.timeInMillis = prevDate
            locationViewModel.setDate(formatter.format(calendar.time))
        }
    }

    fun countData(cookedData: MutableList<LocationModel>): Map<String, Int> {
        return cookedData.groupingBy { it.geoHash!! }.eachCount()
    }

    fun cookData(locationList: List<LocationModel>): Map<String, Int> {
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
        return cookedLocationList.groupingBy { it.geoHash!! }.eachCount()
    }


    override fun cook(time: Long) {
        TODO("Not yet implemented")
    }
}