package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.*


class LocationActivity : AppCompatActivity(), View.OnClickListener, OnChartValueSelectedListener {

    private lateinit var locationCounter: LocationCounter
    private lateinit var locationDatabase: RoomDB
    private val calendar = Calendar.getInstance()
    private var isDateSelected: Boolean = false


    private lateinit var selectDate: Button
    private lateinit var tableView: TableLayout
    private lateinit var barChart: BarChart
    private lateinit var timeView: LinearLayout
    private lateinit var timeViewArrow: ImageView
    private lateinit var countView: LinearLayout
    private lateinit var countViewArrow: ImageView
    private lateinit var res: List<LocationModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        selectDate = findViewById(R.id.selectDate)
        timeView = findViewById(R.id.timeView)
        timeViewArrow = findViewById(R.id.timeViewArrow)
        countView = findViewById(R.id.countView)
        countViewArrow = findViewById(R.id.countViewArrow)
        tableView = findViewById(R.id.tableView)
        barChart = findViewById(R.id.barChart)
        selectDate.setOnClickListener(this)
        timeView.setOnClickListener(this)
        countView.setOnClickListener(this)
        barChart.setOnClickListener(this)
        locationCounter = LocationCounter()
        locationDatabase = RoomDB.getDatabase()!!
        loadData()
        refreshData()
    }

    private suspend fun getData(): List<LocationModel> = withContext(Dispatchers.Default) {
        return@withContext locationDatabase.locationDao().readAll()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.selectDate -> selectDate()
            R.id.timeView -> sortByTime()
            R.id.countView -> sortByCount()
        }
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
        val xAxis: XAxis = barChart.xAxis
        val yAxisL: YAxis = barChart.axisLeft
        val yAxisR: YAxis = barChart.axisRight
        val formatterx: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return labels[value.toInt()]
            }
        }
        data.barWidth = 0.9f // set custom bar width
        yAxisR.isEnabled=false
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = formatterx
        xAxis.position = BOTTOM
        barChart.setDrawGridBackground(false)
        barChart.isAutoScaleMinMaxEnabled=false
        barChart.setDrawGridBackground(false)
        barChart.data = data
        barChart.setFitBars(true)
        barChart.animateY(1000, Easing.EaseOutBack)
        barChart.setOnChartValueSelectedListener(this)
        barChart.invalidate()
    }

    @SuppressLint("ResourceAsColor")
    private fun buildTable(countLocation: Map<String, Int>) {
        tableView.removeAllViews()
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

    private fun loadData() =
        GlobalScope.launch{
                res=getData()
        }

    private fun sortByCount() {
        if (countViewArrow.tag == "down") {
            countViewArrow.tag = "up"
            countViewArrow.setImageResource(R.drawable.ic_arrow_upward)
            refreshData(isDescending = true, isCount = true)

        } else {
            countViewArrow.tag = "down"
            countViewArrow.setImageResource(R.drawable.ic_arrow_downward)
            refreshData(isDescending = false, isCount = true)
        }
    }

    private fun sortByTime() {
        if (timeViewArrow.tag == "down") {
            timeViewArrow.tag = "up"
            timeViewArrow.setImageResource(R.drawable.ic_arrow_upward)
        } else {
            timeViewArrow.tag = "down"
            timeViewArrow.setImageResource(R.drawable.ic_arrow_downward)
        }
        Toast.makeText(this, "by time$res", LENGTH_SHORT).show()
    }

    private fun selectDate() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                Toast.makeText(this, "$dayOfMonth-${monthOfYear + 1}-$year", LENGTH_SHORT).show()
                isDateSelected = true
                refreshData()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun refreshData(
        isCount: Boolean = false,
        isTime: Boolean = false,
        isDescending: Boolean = false
    ) {
        GlobalScope.launch {
            var res = getData()
            var cookedData = locationCounter.countLocation1(res)
            var countedData = cookedData.groupingBy { it.geoHash!! }.eachCount()
            if (isDateSelected){
                res = res.filter { it.time >= calendar.timeInMillis }
                cookedData = locationCounter.countLocation1(res)
                countedData =  cookedData.groupingBy { it.geoHash!! }.eachCount()
            }
            if (res.isNotEmpty()) {
                when {
                    isCount -> {
                        countedData =
                            if (isDescending) countedData.toList().sortedBy { (_, value) -> value }
                                .reversed().toMap()
                            else countedData.toList().sortedBy { (_, value) -> value }.toMap()
                    }
                }
                runOnUiThread {
                    buildGraph(countedData)
                    buildTable(countedData)
                }
            }
            else {
            runOnUiThread{
                    Toast.makeText(
                        this@LocationActivity,
                        "No Data on Selected Date ${calendar.time}",
                        LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Toast.makeText(this, e.toString(), LENGTH_SHORT).show()
        val marker = barChart.marker
        if (e != null) {
            marker.getOffsetForDrawingAtPoint(e.x,e.y)
        }
        marker.refreshContent(e,h)
    }

    override fun onNothingSelected() {
        TODO("Not yet implemented")
    }

}
