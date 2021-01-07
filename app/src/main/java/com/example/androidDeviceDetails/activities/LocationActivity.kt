package com.example.androidDeviceDetails.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.example.androidDeviceDetails.utils.Utils
import com.example.androidDeviceDetails.viewModel.LocationViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.*
import java.util.concurrent.TimeUnit

class LocationActivity : AppCompatActivity(), View.OnClickListener, OnChartValueSelectedListener {
    lateinit var appController: com.example.androidDeviceDetails.controller.AppController<LocationModel>
    lateinit var locationViewModel: LocationViewModel
    private var calendar = Calendar.getInstance()

    private lateinit var binding: ActivityLocationBinding
    private lateinit var selectedRow: TableRow

    companion object {
        const val NAME = "LOCATION_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appController = com.example.androidDeviceDetails.controller.AppController(
            "LOCATION_ACTIVITY",
            binding,
            this
        )
        locationViewModel = appController.viewModel as LocationViewModel
        calendar[Calendar.HOUR] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        binding.selectDate.setOnClickListener(this)
        binding.timeView.setOnClickListener(this)
        binding.countView.setOnClickListener(this)
        binding.barChart.setOnChartValueSelectedListener(this)
        init()
    }

    private fun init() {
        selectedRow = binding.noData
        Toast.makeText(this, calendar.time.toString(), Toast.LENGTH_SHORT).show()
        appController.cook(
            TimeInterval(
                calendar.timeInMillis,
                calendar.timeInMillis + TimeUnit.DAYS.toMillis(1)
            )
        )
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.selectDate -> selectDate()
//            R.id.timeView -> locationController.sortByTime()
            R.id.countView -> {
                if (binding.countViewArrow.tag == "down") {
                    locationViewModel.sortData(true)
                } else
                    locationViewModel.sortData(false)
            }
        }
    }

    private fun selectDate() {
        return Utils.showDatePicker(this)
        { _, year, monthOfYear, dayOfMonth ->
//            locationController.onDateSelect(year, monthOfYear, dayOfMonth)
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = monthOfYear
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            appController.cook(
                TimeInterval(
                    calendar.timeInMillis,
                    calendar.timeInMillis + TimeUnit.DAYS.toMillis(1)
                )
            )
            Toast.makeText(this, calendar.time.toString(), Toast.LENGTH_SHORT).show()
        }.show()
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {
        locationViewModel.onValueSelected(e, selectedRow)
        selectedRow = binding.tableView.findViewWithTag(e?.x?.toInt().toString())
        Log.d("index", "onValueSelected: ${e?.x?.toInt()}")
    }

    override fun onNothingSelected() {
        locationViewModel.onNothingSelected(selectedRow)
    }

}
