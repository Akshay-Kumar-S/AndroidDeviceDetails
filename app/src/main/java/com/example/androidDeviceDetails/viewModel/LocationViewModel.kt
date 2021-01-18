package com.example.androidDeviceDetails.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.example.androidDeviceDetails.utils.SortBy
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry

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


    @SuppressLint("ResourceAsColor")
    fun buildTable(countLocation: Map<String, Int>) {
        binding.tableView.post { binding.tableView.removeAllViews() }
        var index = 1
        for ((k, v) in countLocation) {
            val row = TableRow(context)
            val slNoView = getTextView(index.toString())
            val geoHashTextView = getTextView(k)
            val countTextView = getTextView(v.toString())
            row.addView(slNoView)
            row.addView(geoHashTextView)
            row.addView(countTextView)
            row.tag = (index - 1).toString()
            Log.d("index", "buildTable:$index ")
            binding.tableView.post { binding.tableView.addView(row) }
            index += 1
        }
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
        binding.root.post {
            binding.barChart.setDrawGridBackground(false)
            binding.barChart.isAutoScaleMinMaxEnabled = false
            binding.barChart.setDrawGridBackground(false)
            binding.barChart.data = data
            binding.barChart.setFitBars(true)
            binding.barChart.animateY(1000, Easing.EaseOutBack)
            binding.barChart.invalidate()
        }
    }

    fun onValueSelected(e: Entry?, selectedRow: View) {
        selectedRow.setBackgroundColor(Color.parseColor("#FFFFFF"))
        val newSelectedRow: View = binding.tableView.findViewWithTag(e?.x?.toInt().toString())
        binding.scrollView.scrollTo(0, newSelectedRow.y.toInt())
        newSelectedRow.setBackgroundColor(Color.parseColor("#6FCDF8"))
    }

    fun onNothingSelected(selectedRow: View) {
        selectedRow.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }

    private fun toggleSortButton() {
        if (binding.sortByCountViewArrow.tag == "down") {
            binding.sortByCountViewArrow.tag = "up"
            binding.sortByCountViewArrow.setImageResource(R.drawable.ic_arrow_upward)

        } else {
            binding.sortByCountViewArrow.tag = "down"
            binding.sortByCountViewArrow.setImageResource(R.drawable.ic_arrow_downward)
        }
    }

    private fun onNoData()=
        binding.root.post {
            Toast.makeText(
                context, "No data on selected date", Toast.LENGTH_SHORT
            ).show()
        }

    override fun <T> onData(outputList: ArrayList<T>) {
        cookedDataList = outputList as ArrayList<LocationModel>
        if (cookedDataList.isEmpty())
            onNoData()
        else {
            countedData = cookedDataList.groupingBy { it.geoHash!! }.eachCount()
            buildGraph(countedData)
            buildTable(countedData)
        }
    }

    override fun sort(type: Int) {
        countedData = when (type){
            SortBy.Ascending.ordinal ->  countedData.toList().sortedBy { (_, value) -> value }.toMap()
            else -> countedData.toList().sortedByDescending { (_, value) -> value }.toMap()
        }
        toggleSortButton()
        buildGraph(countedData)
        buildTable(countedData)
    }

}