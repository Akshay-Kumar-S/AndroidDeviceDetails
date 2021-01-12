package com.example.androidDeviceDetails.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.example.androidDeviceDetails.utils.SortBy
import com.example.androidDeviceDetails.viewModel.LocationViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import java.util.*


class LocationActivity : AppCompatActivity(), View.OnClickListener, OnChartValueSelectedListener {
    private lateinit var activityController: ActivityController<LocationModel>
    lateinit var locationViewModel: LocationViewModel
    private var calendar = Calendar.getInstance()

    private lateinit var binding: ActivityLocationBinding
    private lateinit var selectedRow: View

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
        Configuration.getInstance().load(
            applicationContext, getSharedPreferences(
                "my.app.packagename_preferences", Context.MODE_PRIVATE
            )
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        locationViewModel = activityController.viewModel as LocationViewModel
        selectedRow = binding.noData
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
            mapview.setTileSource(TileSourceFactory.MAPNIK)
            mapview.setMultiTouchControls(true)
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
//        locationViewModel.onValueSelected(e, selectedRow)
//        if (e != null) {
//            selectedRow = binding.locationListView.layoutManager?.findViewByPosition(e.x.toInt())!!
//        }
        Log.d("index", "onValueSelected: ${e?.x?.toInt()}")
    }

    override fun onNothingSelected() {
//        locationViewModel.onNothingSelected(selectedRow)
    }

}
