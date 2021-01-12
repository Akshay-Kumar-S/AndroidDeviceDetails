package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.locationModels.CountModel
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.example.androidDeviceDetails.utils.SortBy
import com.github.davidmoten.geo.GeoHash.decodeHash
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker





class LocationViewModel(private val binding: ActivityLocationBinding, val context: Context) :
    BaseViewModel() {

    private lateinit var countedData: Map<String, Int>
    private lateinit var cookedDataList: ArrayList<LocationModel>


    private fun getTextView(text: String): TextView {
        val textView = TextView(context)
        val tlp = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tlp.weight = 0.3F
        textView.layoutParams = tlp
        textView.gravity = Gravity.CENTER
        textView.text = text
        textView.setTextColor(Color.parseColor("#000000"))
        return textView
    }


//    private fun buildGraph(countData: Map<String, Int>) {
//        val entries: MutableList<BarEntry> = emptyList<BarEntry>().toMutableList()
//        val labels = emptyList<String>().toMutableList()
//        var index = 0f
//        for ((k, v) in countData) {
//            entries.add(BarEntry(index, v.toFloat()))
//            labels.add(k)
//            index += 1
//        }
//        val valueFormatter: ValueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return value.toInt().toString()
//            }
//        }
//        val set = BarDataSet(entries, "Location Count")
//        set.valueFormatter=valueFormatter
//        val data = BarData(set)
//        val description: Description = binding.barChart.description
//        description.isEnabled = false
//        val yAxisL: YAxis = binding.barChart.axisLeft
//        val yAxisR: YAxis = binding.barChart.axisRight
//        val xAxis: XAxis = binding.barChart.xAxis
////        val formatterX: ValueFormatter = object : ValueFormatter() {
////            override fun getAxisLabel(value: Float, axis: AxisBase): String {
////                Log.d("TAG", "getAxisLabel: ${labels[value.toInt()]} ")
////                return labels[value.toInt()]
////            }
////        }
//
//        data.barWidth = 0.9f // set custom bar width
//        yAxisR.isEnabled = false
//        yAxisL.isEnabled=false
//        xAxis.setDrawLabels(false)
//        xAxis.setDrawGridLines(false)
//
//        binding.root.post {
//            binding.barChart.isAutoScaleMinMaxEnabled = true
//            binding.barChart.data = data
//            binding.barChart.setFitBars(true)
//            binding.barChart.animateY(1000, Easing.EaseOutBack)
//            binding.barChart.invalidate()
//        }
//    }

    fun onValueSelected(e: Entry?, selectedRow: View) {
//        selectedRow.setBackgroundColor(Color.parseColor("#FFFFFF"))
//        if (e != null) {
////            binding.locationListView.layoutManager?.scrollToPosition(e.x.toInt())
//        }
//        e?.x?.let {
////            binding.locationListView.layoutManager?.findViewByPosition(
//                it.toInt())
//        }?.setBackgroundColor(Color.parseColor("#6FCDF8"))
    }

    fun onNothingSelected(selectedRow: View) {
//        selectedRow.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }

//    private fun toggleSortButton() {
//        if (binding.sortByCountViewArrow.tag == "down") {
//            binding.sortByCountViewArrow.tag = "up"
//            binding.sortByCountViewArrow.setImageResource(R.drawable.ic_arrow_upward)
//
//        } else {
//            binding.sortByCountViewArrow.tag = "down"
//            binding.sortByCountViewArrow.setImageResource(R.drawable.ic_arrow_downward)
//        }
//    }

    private fun onNoData() =
        binding.root.post {
            Toast.makeText(
                context, "No data on selected date", Toast.LENGTH_SHORT
            ).show()
        }

    override fun sort(type: Int) {
        countedData = when (type) {
            SortBy.Ascending.ordinal -> countedData.toList().sortedBy { (_, value) -> value }
                .toMap()
            else -> countedData.toList().sortedByDescending { (_, value) -> value }.toMap()
        }
        Log.d("Counted Data", "onDataCount:${countedData.size} ")
//        toggleSortButton()
//        buildGraph(countedData)
    }

    override fun <T> onData(outputList: ArrayList<T>) {
        cookedDataList = outputList as ArrayList<LocationModel>
        if (cookedDataList.isEmpty())
            onNoData()
        else {
            countedData = cookedDataList.groupingBy { it.geoHash!! }.eachCount()
            Log.d("Counted Data", "onData:${countedData.size} ")
//            binding.noData.visibility=GONE
            initMap(countedData.keys.elementAt(0))
            buildAdapterView(countedData)
//            buildGraph(countedData)
            addPointOnMap(countedData)
        }
    }

    private fun buildAdapterView(dataList: Map<String, Int>) {
        val countList: ArrayList<CountModel> = ArrayList()
        if (dataList.isNotEmpty()) {
            for (i in dataList){
                val latLong = decodeHash(i.key)
                val address = Geocoder(context).getFromLocation(latLong.lat, latLong.lon, 1)[0].locality?.toString()
                countList.add(CountModel(i.key, i.value, address ?: "cannot locate"))
            }
            binding.root.post {
                binding.bottomLocation.locationListView.layoutManager= LinearLayoutManager(context)
                binding.bottomLocation.locationListView.adapter = LocationAdapter(countList)
            }
        } else {
            countList.add(CountModel("No Data", 0, ""))
            binding.root.post {
                binding.bottomLocation.locationListView.layoutManager = LinearLayoutManager(context)
                binding.bottomLocation.locationListView.adapter = LocationAdapter(countList)
            }
        }
    }

    private fun addPointOnMap(data: Map<String, Int>){
        for (geoHash in data) {
            val latLong = decodeHash(geoHash.key)
            val geoPoint = GeoPoint(latLong.lat, latLong.lon)
            val address = Geocoder(context).getFromLocation(latLong.lat, latLong.lon, 1)[0].locality?.toString()
            Log.d("TAG", "addPointOnMap: $address")
            val startMarker = Marker(binding.mapview)
            binding.root.post{
                startMarker.position = geoPoint
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                binding.mapview.overlays.add(startMarker) }
        }
    }

    private fun initMap(param: String) {
        val latLong = decodeHash(param)
        val geoPoint= GeoPoint(latLong.lat,latLong.lon)
        binding.root.post{
            val mapController = binding.mapview.controller
            mapController.setZoom(10.0)
            mapController.setCenter(geoPoint)
        }
    }

}