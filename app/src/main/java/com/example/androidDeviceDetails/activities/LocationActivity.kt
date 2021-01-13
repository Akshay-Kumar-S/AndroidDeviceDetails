package com.example.androidDeviceDetails.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.locationModels.CountModel
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.example.androidDeviceDetails.utils.SortBy
import com.example.androidDeviceDetails.viewModel.LocationViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsDisplay
import java.util.*
import kotlin.collections.ArrayList


class LocationActivity : AppCompatActivity(), View.OnClickListener, OnChartValueSelectedListener {
    private lateinit var activityController: ActivityController<LocationModel>
    lateinit var locationViewModel: LocationViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var binding: ActivityLocationBinding
    private lateinit var selectedRow: View

    companion object {
        const val NAME = "LOCATION_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val arrayList = ArrayList<CountModel>()
        arrayList.add(CountModel("NoData",0,""))
        binding.bottomLocation.locationListView.adapter = LocationAdapter(arrayList)
        initBottomSheet()
        activityController = ActivityController(
            NAME,
            binding,
            this,
            binding.bottomLocation.dateTimePickerLayout,
            supportFragmentManager
        )
        Configuration.getInstance().load(
            applicationContext, getSharedPreferences(
                "my.app.packagename_preferences", Context.MODE_PRIVATE
            )
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        locationViewModel = activityController.viewModel as LocationViewModel
//        selectedRow = binding.bottomLocation.noData
        initDatePicker()
        initMapview()
        binding.apply {
            bottomLocation.countView.setOnClickListener(this@LocationActivity)
//            bottomLocation.barChart.setOnChartValueSelectedListener(this@LocationActivity)
        }
    }

    private fun initMapview() {
        binding.apply{
            mapview.setTileSource(TileSourceFactory.MAPNIK)
            mapview.setMultiTouchControls(true)
            mapview.zoomController.display.setPositions(
                false,
                CustomZoomButtonsDisplay.HorizontalPosition.RIGHT,
                CustomZoomButtonsDisplay.VerticalPosition.CENTER
            )
        }
    }

    private fun initDatePicker() {
        binding.apply{
            bottomLocation.dateTimePickerLayout.startTime
                .setOnClickListener(this@LocationActivity)
            bottomLocation.dateTimePickerLayout.startDate
                .setOnClickListener(this@LocationActivity)
            bottomLocation.dateTimePickerLayout.endTime
                .setOnClickListener(this@LocationActivity)
            bottomLocation.dateTimePickerLayout.endDate
                .setOnClickListener(this@LocationActivity)
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomLocation.bottomSheet)
        bottomSheetBehavior.peekHeight = 300
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.countView -> {
                if (binding.bottomLocation.sortByCountViewArrow.tag == "down") {
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