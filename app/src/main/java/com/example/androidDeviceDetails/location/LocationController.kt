package com.example.androidDeviceDetails.location

//import android.annotation.SuppressLint
//import android.content.Context
//import android.util.Log
//import android.view.View
//import android.widget.TableRow
//import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
//import com.example.androidDeviceDetails.location.models.LocationModel
//import com.example.androidDeviceDetails.models.RoomDB
//import com.github.mikephil.charting.data.Entry
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.TimeUnit
//
//
//class LocationController(val context: Context, binding: ActivityLocationBinding) {
//    private var locationCooker: LocationCooker = LocationCooker()
//    private var locationViewModel: LocationViewModel = LocationViewModel(context, binding)
//
//    private val calendar = Calendar.getInstance()
//
//    @SuppressLint("SimpleDateFormat")
//    private val formatter = SimpleDateFormat("dd-MM-yyyy")
//
//
//
//
//
//
//    private fun refreshData(
//        isCount: Boolean = false,
////        isTime: Boolean = false,
//        isDescending: Boolean = false
//    ) {
//        when {
//            isCount -> countedData = locationCooker.sortDate(countedData, isDescending)
//        }
//        locationViewModel.buildGraph(countedData)
//        locationViewModel.buildTable(countedData)
//    }
//
//    fun onValueSelected(e: Entry?, selectedRow: TableRow) {
//        locationViewModel.onValueSelected(e, selectedRow)
//    }
//
//    fun onNothingSelected(selectedRow: View) {
//        locationViewModel.onNothingSelected(selectedRow)
//    }
//
//    fun onDateSelect(year: Int, monthOfYear: Int, dayOfMonth: Int) {
//        prevDate = calendar.timeInMillis
//        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
//        loadData(calendar.timeInMillis)
//    }
//
//    fun sortByTime() {
//        locationViewModel.toggleSortButton()
//    }
//
//    fun sortByCount(isDescending: Boolean) {
//        locationViewModel.toggleSortButton()
//        if (isDescending) {
//            refreshData(isDescending = true, isCount = true)
//        } else {
//            refreshData(isDescending = false, isCount = true)
//        }
//    }
//}