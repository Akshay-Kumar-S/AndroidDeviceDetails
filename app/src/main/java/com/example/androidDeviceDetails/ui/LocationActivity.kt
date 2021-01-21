package com.example.androidDeviceDetails.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.interfaces.OnItemClickListener
import com.example.androidDeviceDetails.models.location.LocationDisplayModel
import com.example.androidDeviceDetails.utils.SortBy
import com.example.androidDeviceDetails.viewModel.LocationViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsDisplay.HorizontalPosition.RIGHT
import org.osmdroid.views.CustomZoomButtonsDisplay.VerticalPosition.CENTER
import org.osmdroid.views.MapView.getTileSystem
import java.util.*
import kotlin.collections.ArrayList


class LocationActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener {
    private lateinit var activityController: ActivityController<LocationDisplayModel>
    lateinit var locationViewModel: LocationViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var binding: ActivityLocationBinding

    companion object {
        const val NAME = "LOCATION_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
        initBottomSheet()
        activityController = ActivityController(
            NAME, binding, this,
            binding.bottomLocation.dateTimePickerLayout, supportFragmentManager
        )
        locationViewModel = activityController.viewModel as LocationViewModel
        initDatePicker()
        initMap()
        binding.apply {
            bottomLocation.sortButton.setOnClickListener(this@LocationActivity)
        }
    }

    private fun initRecyclerView() {
        val arrayList = ArrayList<LocationDisplayModel>()
        arrayList.add(LocationDisplayModel("NoData", 0, ""))
        binding.bottomLocation.locationListView.adapter = LocationAdapter(arrayList, this)
        binding.bottomLocation.locationListView.isNestedScrollingEnabled = true
    }

    private fun initMap() {
        Configuration.getInstance().load(
            applicationContext, getSharedPreferences(
                "my.app.packagename_preferences", Context.MODE_PRIVATE
            )
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        binding.apply {
            mapView.isHorizontalMapRepetitionEnabled = true
            mapView.isVerticalMapRepetitionEnabled = false
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            mapView.isTilesScaledToDpi = true
            mapView.minZoomLevel = 2.0
            mapView.setScrollableAreaLimitLatitude(
                getTileSystem().maxLatitude,
                getTileSystem().minLatitude,
                0
            )
            mapView.zoomController.display.setPositions(false, RIGHT, CENTER)
        }
    }

    private fun initDatePicker() {
        binding.apply {
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
            R.id.sortButton -> {
                if (binding.bottomLocation.sortButton.tag == "down") {
                    activityController.sortView(SortBy.Descending.ordinal)
                } else
                    activityController.sortView(SortBy.ASCENDING.ordinal)
            }
            R.id.startTime -> activityController.setTime(this, R.id.startTime)
            R.id.startDate -> activityController.setDate(this, R.id.startDate)
            R.id.endTime -> activityController.setTime(this, R.id.endTime)
            R.id.endDate -> activityController.setDate(this, R.id.endDate)
        }
    }

    override fun onItemClicked(clickedItem: LocationDisplayModel) {
        locationViewModel.focusMapTo(clickedItem.geoHash)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}
