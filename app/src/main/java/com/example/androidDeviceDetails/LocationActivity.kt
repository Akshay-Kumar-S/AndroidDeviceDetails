package com.example.androidDeviceDetails

import android.Manifest
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.models.CountModel
import com.example.androidDeviceDetails.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.fonfon.kgeohash.GeoHash
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


private const val PERMISSION_REQUEST = 10

class LocationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var locationCounter: LocationCounter
    private lateinit var locationDatabase: RoomDB

    private lateinit var geoHashView: TextView
    private lateinit var countLocation: Button
    private lateinit var tableView: TableLayout
    private lateinit var barChart: BarChart

    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        geoHashView = findViewById(R.id.geoHashView)
        countLocation = findViewById(R.id.countLocation)
        tableView = findViewById(R.id.tableView)
        barChart = findViewById(R.id.barChart)
        countLocation.setOnClickListener(this)
        locationCounter = LocationCounter()
        locationDatabase = RoomDB.getDatabase()!!
    }

    private suspend fun getData(): List<LocationModel> = withContext(Dispatchers.IO) {
        return@withContext locationDatabase.locationDao().readAll()
    }

    private fun getTextView(text: String): TextView {
        val textView = TextView(this)
        val tlp = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tlp.weight = 0.5F
        textView.layoutParams = tlp
        textView.gravity = Gravity.CENTER
        textView.text = text
        return textView
    }

    private fun buildGraph(res: Map<String, Int>) {
        val entries: MutableList<BarEntry> = emptyList<BarEntry>().toMutableList()
        val labels = emptyArray<String>().toMutableList()
        var index = 0f
        for ((k, v) in res){
            entries.add(BarEntry(index, v.toFloat()))
            labels.add(k)
            index+=1
        }
        val set = BarDataSet(entries,"Location Count")
        val data = BarData(set)
        data.barWidth = 0.9f // set custom bar width
        barChart.setDrawGridBackground(false)
        barChart.data = data
        barChart.description = Description()
        barChart.setDrawGridBackground(false)
        barChart.setFitBars(true) // make the x-axis fit exactly all bars
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return labels[value.toInt()]
            }
        }
        val xAxis: XAxis = barChart.xAxis
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = formatter
        xAxis.position= BOTTOM
        barChart.invalidate() // refresh
    }

    private fun buildTable(countLocation: Map<String, Int>) {
        tableView.removeAllViews()
        for ((k, v) in countLocation) {
            val row = TableRow(this)
            val lp = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            row.layoutParams = lp
            val geoHashTextView = getTextView(k)
            val countTextView = getTextView(v.toString())
            row.addView(geoHashTextView)
            row.addView(countTextView)
            tableView.addView(row)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.countLocation -> {
                GlobalScope.launch {
                    val res = getData()
                    geoHashView.post {
                        buildGraph(locationCounter.countLocation(res))
                        buildTable(locationCounter.countLocation(res))
                        geoHashView.text = locationCounter.countLocation(res).toString()
                    }
                }
            }
        }
    }

}