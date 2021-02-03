package com.example.androidDeviceDetails.ui

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.fragments.SortBySheet
import com.example.androidDeviceDetails.interfaces.OnItemClickListener
import com.example.androidDeviceDetails.models.location.LocationData
import com.example.androidDeviceDetails.utils.ColorFilter
import com.example.androidDeviceDetails.utils.DateTimePicker
import com.example.androidDeviceDetails.utils.SortBy
import com.example.androidDeviceDetails.viewModel.LocationViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsDisplay.HorizontalPosition.RIGHT
import org.osmdroid.views.CustomZoomButtonsDisplay.VerticalPosition.CENTER
import org.osmdroid.views.MapView.getTileSystem
import org.osmdroid.config.Configuration as osmConfig


class LocationActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var activityController: ActivityController<LocationData>
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var binding: ActivityLocationBinding
    private lateinit var sortBySheet: SortBySheet

    companion object {
        const val NAME = "LOCATION_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location)
        initRecyclerView()
        initBottomSheet()
        initMap()
        activityController = ActivityController(this, NAME, binding)
        locationViewModel = activityController.viewModel as LocationViewModel
        sortBySheet = SortBySheet(options, activityController::sortView, SortBy.ASCENDING.ordinal)
        DateTimePicker(
            this, binding.locationBottomSheet.dateTimePicker,
            activityController::setTime, activityController::setDate
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "Sort By") sortBySheet.show(supportFragmentManager, "Sort By")
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        val arrayList = ArrayList<LocationData>()
        binding.locationBottomSheet.locationListView.adapter = LocationAdapter(arrayList, this)
        binding.locationBottomSheet.locationListView.isNestedScrollingEnabled = true
    }

    private fun initMap() {
        osmConfig.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        binding.mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            zoomController.display.setPositions(false, RIGHT, CENTER)
            isHorizontalMapRepetitionEnabled = true
            isVerticalMapRepetitionEnabled = false
            setMultiTouchControls(true)
            isTilesScaledToDpi = true
            controller.setZoom(2.0)
            minZoomLevel = 2.0
            setScrollableAreaLimitLatitude(
                getTileSystem().maxLatitude, getTileSystem().minLatitude, 0
            )
            when (resources?.configuration?.uiMode?.and(UI_MODE_NIGHT_MASK)) {
                UI_MODE_NIGHT_YES -> overlayManager.tilesOverlay.setColorFilter(ColorFilter.darkModeFilter)
            }
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.locationBottomSheet.bottomSheet)
        bottomSheetBehavior.peekHeight = 300
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    override fun onItemClicked(clickedItem: LocationData) {
        locationViewModel.focusMapTo(clickedItem.latitude, clickedItem.longitude)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private val options = arrayListOf(
        "Count (Most visited first)" to SortBy.DESCENDING.ordinal,
        "Count (Least visited first)" to SortBy.ASCENDING.ordinal,
        "Duration (Largest first)" to SortBy.TIME_DESCENDING.ordinal,
        "Duration (Smallest first)" to SortBy.TIME_ASCENDING.ordinal
    )
}
