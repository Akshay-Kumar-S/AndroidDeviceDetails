package com.example.androidDeviceDetails.location

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TableRow
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.location.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class LocationController(val context: Context, binding: ActivityLocationBinding) {
    private var locationCooker: LocationCooker = LocationCooker()
    private var locationViewModel: LocationViewModel = LocationViewModel(context, binding)
    private lateinit var res: List<LocationModel>
    private lateinit var cookedData: MutableList<LocationModel>
    private lateinit var countedData: Map<String, Int>
    private var locationDatabase: RoomDB = RoomDB.getDatabase()!!
    private var prevDate: Long = 0L
    private val calendar = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat")
    private val formatter = SimpleDateFormat("dd-MM-yyyy")


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
                cookedData = locationCooker.cookData(res)
                countedData = locationCooker.countData(cookedData)
                refreshData()
            } else {
                locationViewModel.toast("No Data on Selected Date ${formatter.format(calendar.time)}")
                calendar.timeInMillis = prevDate
                locationViewModel.setDate(formatter.format(calendar.time))
            }
        }

    private fun refreshData(
        isCount: Boolean = false,
//        isTime: Boolean = false,
        isDescending: Boolean = false
    ) {
        when {
            isCount -> {
                countedData =
                    if (isDescending) countedData.toList().sortedBy { (_, value) -> value }
                        .reversed().toMap()
                    else countedData.toList().sortedBy { (_, value) -> value }.toMap()
            }
        }
        locationViewModel.buildGraph(countedData)
        locationViewModel.buildTable(countedData)
    }

    fun onValueSelected(e: Entry?, selectedRow: TableRow) {
        locationViewModel.onValueSelected(e, selectedRow)
    }

    fun onNothingSelected(selectedRow: View) {
        locationViewModel.onNothingSelected(selectedRow)
    }

    fun onDateSelect(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        prevDate = calendar.timeInMillis
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
        loadData(calendar.timeInMillis)
        refreshData()
    }

    fun sortByTime() {
        locationViewModel.toggleSortButton(false)
    }

    fun sortByCount(isDescending: Boolean) {
        locationViewModel.toggleSortButton(isDescending)
        if (isDescending) {
            refreshData(isDescending = true, isCount = true)
        } else {
            refreshData(isDescending = false, isCount = true)
        }
    }
}