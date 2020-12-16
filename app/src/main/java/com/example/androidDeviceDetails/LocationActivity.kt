package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import android.widget.TableLayout
import android.widget.Toast.*
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
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


class LocationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var locationCounter: LocationCounter
    private lateinit var locationDatabase: RoomDB
    private  var mYear = Calendar.YEAR
    private  var mMonth = Calendar.MONTH
    private  var mDay = Calendar.DAY_OF_MONTH

    private lateinit var selectDate: Button
    private lateinit var tableView: TableLayout
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        selectDate = findViewById(R.id.selectDate)
        tableView = findViewById(R.id.tableView)
        barChart = findViewById(R.id.barChart)
        selectDate.setOnClickListener(this)
        locationCounter = LocationCounter()
        locationDatabase = RoomDB.getDatabase()!!
        refreshData()
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
        tlp.weight = 0.3F
        textView.layoutParams = tlp
        textView.gravity = Gravity.CENTER
        textView.text = text
        return textView
    }

    private fun buildGraph(res: Map<String, Int>) {
        val entries: MutableList<BarEntry> = emptyList<BarEntry>().toMutableList()
        val labels = emptyList<String>().toMutableList()
        var index = 0f
        for ((k, v) in res) {
            entries.add(BarEntry(index, v.toFloat()))
            labels.add(k)
            index += 1
        }
        val set = BarDataSet(entries, "Location Count")
        val data = BarData(set)
        data.barWidth = 0.9f // set custom bar width
        barChart.data = data
        barChart.setFitBars(true)
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return labels[value.toInt()]
            }
        }
        val xAxis: XAxis = barChart.xAxis
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = formatter
        xAxis.position = BOTTOM
        barChart.invalidate()
    }

    @SuppressLint("ResourceAsColor")
    private fun buildTable(countLocation: Map<String, Int>) {
        tableView.removeAllViews()
        val headerRow = TableRow(this)
        headerRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        val slNo = getTextView("Sl No.")
        slNo.setBackgroundColor(R.color.labelBackGround)
        val geoHashLabel = getTextView("GeoHash")
        geoHashLabel.setBackgroundColor(R.color.labelBackGround)
        val countLabel = getTextView("Count")
        countLabel.setBackgroundColor(R.color.labelBackGround)
        headerRow.addView(slNo)
        headerRow.addView(geoHashLabel)
        headerRow.addView(countLabel)
        tableView.addView(headerRow)
        var index = 1
        for ((k, v) in countLocation) {
            val row = TableRow(this)
            val slNoView = getTextView(index.toString())
            val geoHashTextView = getTextView(k)
            val countTextView = getTextView(v.toString())
            row.addView(slNoView)
            row.addView(geoHashTextView)
            row.addView(countTextView)
            tableView.addView(row)
            index += 1
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.selectDate -> {
                val c = Calendar.getInstance()
                mYear = c[Calendar.YEAR]
                mMonth = c[Calendar.MONTH]
                mDay = c[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog(
                        this, { _, year, monthOfYear, dayOfMonth ->
                        mYear=year
                        mMonth=monthOfYear
                        mDay=dayOfMonth
                        makeText(this,mDay.toString() + "-" + (mMonth + 1) + "-" + mYear, LENGTH_SHORT).show()
                        },
                        mYear, mMonth, mDay
                    )
                    datePickerDialog.show()
                refreshData()
            }
        }
    }

    private fun refreshData() {
        GlobalScope.launch {
            val res = getData()
            runOnUiThread {
                buildGraph(locationCounter.countLocation(res))
                buildTable(locationCounter.countLocation(res))
            }
        }
    }

}