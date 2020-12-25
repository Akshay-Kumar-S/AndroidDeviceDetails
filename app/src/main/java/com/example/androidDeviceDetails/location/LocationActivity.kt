package com.example.androidDeviceDetails.location

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.AppController
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.utils.Utils
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class LocationActivity : AppCompatActivity(), View.OnClickListener, OnChartValueSelectedListener {

    private lateinit var locationController: AppController
    private lateinit var binding: ActivityLocationBinding
    private lateinit var selectedRow: TableRow
    private var locationViewModel: LocationViewModel = LocationViewModel(this, binding)

    companion object{
        const val NAME="LOCATION_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationController = LocationController(this, binding)
        binding.selectDate.setOnClickListener(this)
        binding.timeView.setOnClickListener(this)
        binding.countView.setOnClickListener(this)
        binding.barChart.setOnChartValueSelectedListener(this)
        init()
    }

    private fun init() {
        selectedRow = binding.noData
        locationController.loadData()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.selectDate -> selectDate()
            R.id.timeView -> locationController.sortByTime()
            R.id.countView -> {
                if (binding.countViewArrow.tag == "down") {
                    locationController.sortByCount(true)
                } else
                    locationController.sortByCount(false)

            }
        }
    }

    private fun selectDate() {
        return Utils.showDatePicker(this)
        { _, year, monthOfYear, dayOfMonth ->
            locationController.onDateSelect(year, monthOfYear, dayOfMonth)
        }.show()
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {
        locationController.onValueSelected(e, selectedRow)
        selectedRow = binding.tableView.findViewWithTag(e?.x?.toInt().toString())
        Log.d("index", "onValueSelected: ${e?.x?.toInt()}")
    }

    override fun onNothingSelected() {
        locationController.onNothingSelected(selectedRow)
    }

}
