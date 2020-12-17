package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
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
    private var mYear = Calendar.YEAR
    private var mMonth = Calendar.MONTH
    private var mDay = Calendar.DAY_OF_MONTH

    private lateinit var selectDate: Button
    private lateinit var tableView: TableLayout
    private lateinit var barChart: BarChart
    private lateinit var timeView: LinearLayout
    private lateinit var timeViewArrow: ImageView
    private lateinit var countView: LinearLayout
    private lateinit var countViewArrow: ImageView

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
        locationCounter = LocationCounter()
        locationDatabase = RoomDB.getDatabase()!!
        refreshData()
    }

    private suspend fun getData(): List<LocationModel> = withContext(Dispatchers.IO) {
        return@withContext locationDatabase.locationDao().readAll()
    }

//    private suspend fun getDataOnDate(time:String): List<LocationModel> = withContext(Dispatchers.IO) {
//        return@withContext locationDatabase.locationDao().selectDataOn(time)
//    }

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
        data.barWidth = 0.9f // set custom bar width
        barChart.setDrawGridBackground(false)
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

    private fun sortByCount() {
        if (countViewArrow.tag == "down") {
            countViewArrow.tag = "up"
            countViewArrow.setImageResource(R.drawable.ic_arrow_upward)
            refreshData(isDescending = false,isCount = true)

        } else {
            countViewArrow.tag = "down"
            countViewArrow.setImageResource(R.drawable.ic_arrow_downward)
            refreshData(isDescending = true,isCount = true)
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
        makeText(this, "by time", LENGTH_SHORT).show()
    }

    private fun selectDate() {
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
                mYear = year
                mMonth = monthOfYear
                mDay = dayOfMonth
                makeText(
                    this,
                    mDay.toString() + "-" + (mMonth + 1) + "-" + mYear,
                    LENGTH_SHORT
                ).show()
            },
            mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun refreshData(
        isCount: Boolean = false,
        isTime: Boolean = false,
        isDescending: Boolean = false
    ) {
        GlobalScope.launch {
            val res = getData()
            var countedData = locationCounter.countLocation(res)
            when {
                isCount -> {
                    countedData = if (isDescending)
                                        countedData.toList().sortedBy { (_,value) -> value}.reversed().toMap()
                                    else
                                         countedData.toList().sortedBy { (_,value) -> value}.toMap()
                }
            }
            runOnUiThread {
                buildGraph(countedData)
                buildTable(countedData)
            }
        }
    }

}