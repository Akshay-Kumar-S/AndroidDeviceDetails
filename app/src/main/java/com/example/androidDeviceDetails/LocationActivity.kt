package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.LocationModel
import com.example.androidDeviceDetails.models.RoomDB
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@SuppressLint("SimpleDateFormat")
class LocationActivity : AppCompatActivity(), View.OnClickListener, OnChartValueSelectedListener {

    private lateinit var locationCooker: LocationCooker
    private lateinit var locationDatabase: RoomDB
    private val calendar = Calendar.getInstance()
    private var prevDate: Long = 0L
    private val formatter = SimpleDateFormat("dd-MM-yyyy")

    private lateinit var binding: ActivityLocationBinding
    private lateinit var selectedRow: TableRow


    private lateinit var res: List<LocationModel>
    private lateinit var cookedData: MutableList<LocationModel>
    private lateinit var countedData: Map<String, Int>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.selectDate.setOnClickListener(this)
        binding.timeView.setOnClickListener(this)
        binding.countView.setOnClickListener(this)
        binding.barChart.setOnClickListener(this)
        locationCooker = LocationCooker()
        locationDatabase = RoomDB.getDatabase()!!
        binding.selectDate.text = getString(R.string.tillToday)
        loadData()
    }

    private suspend fun getData(date: Long?): List<LocationModel> =
        withContext(Dispatchers.Default) {
            if (date != null) {
                return@withContext locationDatabase.locationDao()
                    .readDataFromDate(date, date + TimeUnit.DAYS.toMillis(1))
            }
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
        textView.setTextColor(Color.parseColor("#000000"))
        return textView
    }

    private fun buildGraph(countData: Map<String, Int>) {
        val entries: MutableList<BarEntry> = emptyList<BarEntry>().toMutableList()
        val labels = emptyList<String>().toMutableList()
        var index = 0f
        for ((k, v) in countData) {
            entries.add(BarEntry(index, v.toFloat()))
            labels.add(k)
            index += 1
        }
        val set = BarDataSet(entries, "Location Count")
        val data = BarData(set)
//        val yAxisL: YAxis = binding.barChart.axisLeft
        val yAxisR: YAxis = binding.barChart.axisRight
//        val formatterX: ValueFormatter = object : ValueFormatter() {
//            override fun getAxisLabel(value: Float, axis: AxisBase): String {
//                return labels[value.toInt()]
//            }
//        }
        data.barWidth = 0.9f // set custom bar width
        yAxisR.isEnabled = false
        binding.barChart.setDrawGridBackground(false)
        binding.barChart.isAutoScaleMinMaxEnabled = false
        binding.barChart.setDrawGridBackground(false)
        binding.barChart.data = data
        binding.barChart.setFitBars(true)
        binding.barChart.animateY(1000, Easing.EaseOutBack)
        binding.barChart.setOnChartValueSelectedListener(this)
        binding.barChart.invalidate()
    }

    @SuppressLint("ResourceAsColor")
    private fun buildTable(countLocation: Map<String, Int>) {
        binding.tableView.removeAllViews()
        var index = 1
        for ((k, v) in countLocation) {
            val row = TableRow(this)
            val slNoView = getTextView(index.toString())
            val geoHashTextView = getTextView(k)
            val countTextView = getTextView(v.toString())
            row.addView(slNoView)
            row.addView(geoHashTextView)
            row.addView(countTextView)
            row.tag = (index - 1).toString()
            Log.d("index", "buildTable:$index ")
            binding.tableView.addView(row)
            index += 1
        }
    }

    private fun loadData(date: Long? = null) =
        GlobalScope.launch {
            res = getData(date)
            Log.d("LocationData", "loadData: $res")
            if (res.isNotEmpty()) {
                binding.selectDate.post {
                    binding.selectDate.text = formatter.format(calendar.time)
                }
                cookedData = locationCooker.cookData(res)
                countedData = locationCooker.countData(cookedData)
                refreshData()
            } else {
                runOnUiThread {
                    Toast.makeText(
                        this@LocationActivity,
                        "No Data on Selected Date ${formatter.format(calendar.time)}", LENGTH_SHORT
                    ).show()
                    calendar.timeInMillis = prevDate
                    binding.selectDate.text = formatter.format(calendar.time)
                }
            }
        }

    private fun sortByCount() {
        if (binding.countViewArrow.tag == "down") {
            binding.countViewArrow.tag = "up"
            binding.countViewArrow.setImageResource(R.drawable.ic_arrow_upward)
            refreshData(isDescending = true, isCount = true)

        } else {
            binding.countViewArrow.tag = "down"
            binding.countViewArrow.setImageResource(R.drawable.ic_arrow_downward)
            refreshData(isDescending = false, isCount = true)
        }
    }

    private fun sortByTime() {
        if (binding.timeViewArrow.tag == "down") {
            binding.timeViewArrow.tag = "up"
            binding.timeViewArrow.setImageResource(R.drawable.ic_arrow_upward)
        } else {
            binding.timeViewArrow.tag = "down"
            binding.timeViewArrow.setImageResource(R.drawable.ic_arrow_downward)
        }
//        Toast.makeText(this, "by time", LENGTH_SHORT).show()
    }

    private fun selectDate() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                prevDate = calendar.timeInMillis
                calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                loadData(calendar.timeInMillis)
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
        runOnUiThread {
            buildGraph(countedData)
            buildTable(countedData)
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (this::selectedRow.isInitialized) {
            selectedRow.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        selectedRow = binding.tableView.findViewWithTag(e?.x?.toInt().toString())
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            binding.scrollView.scrollToDescendant(selectedRow)
        } else
            e?.x?.let { binding.scrollView.scrollTo(0, it.toInt()) }
        selectedRow.setBackgroundColor(Color.parseColor("#6FCDF8"))
        Log.d("index", "onValueSelected: ${e?.x?.toInt()}")
    }

    override fun onNothingSelected() {
        selectedRow.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }

}
