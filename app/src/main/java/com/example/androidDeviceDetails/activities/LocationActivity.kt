package com.example.androidDeviceDetails.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.TimePeriod
import com.example.androidDeviceDetails.models.locationModels.CountModel
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.example.androidDeviceDetails.utils.SortBy
import com.example.androidDeviceDetails.viewModel.LocationViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.*
import java.util.concurrent.TimeUnit

class LocationActivity : AppCompatActivity(), View.OnClickListener, OnChartValueSelectedListener {
    lateinit var activityController: ActivityController<LocationModel>
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
        activityController = ActivityController(
            NAME,
            binding,
            this,
            binding.dateTimePickerLayout,
            supportFragmentManager
        )
//        val array : Array<CountModel> = emptyArray()
//        array.plus(CountModel("sdfsa",5,"dsfgdsfg"))
//        binding.locationListView.adapter = LocationAdapter(array)
        locationViewModel = activityController.viewModel as LocationViewModel
        calendar[Calendar.HOUR] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        binding.apply {
            dateTimePickerLayout.startTime
                .setOnClickListener(this@LocationActivity)
            dateTimePickerLayout.startDate
                .setOnClickListener(this@LocationActivity)
            dateTimePickerLayout.endTime
                .setOnClickListener(this@LocationActivity)
            dateTimePickerLayout.endDate
                .setOnClickListener(this@LocationActivity)
            countView.setOnClickListener(this@LocationActivity)
            barChart.setOnChartValueSelectedListener(this@LocationActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.countView -> {
                if (binding.sortByCountViewArrow.tag == "down") {
                    activityController.sortView(SortBy.Descending.ordinal)
                } else
                    activityController.sortView(SortBy.Ascending.ordinal)
            }
            R.id.startTime -> activityController.setStartTime(this)
            R.id.startDate -> activityController.setStartDate(this)
            R.id.endTime -> activityController.setEndTime(this)
            R.id.endDate -> activityController.setEndDate(this)
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        locationViewModel.onValueSelected(e, selectedRow)
//        selectedRow = binding.tableView.findViewWithTag(e?.x?.toInt().toString())
//        Log.d("index", "onValueSelected: ${e?.x?.toInt()}")
    }

    override fun onNothingSelected() {
        locationViewModel.onNothingSelected(selectedRow)
    }

}
