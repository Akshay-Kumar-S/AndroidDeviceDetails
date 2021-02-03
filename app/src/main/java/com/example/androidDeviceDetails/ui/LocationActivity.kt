package com.example.androidDeviceDetails.ui

import android.content.res.Configuration.*
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.interfaces.OnItemClickListener
import com.example.androidDeviceDetails.models.location.LocationData
import com.example.androidDeviceDetails.utils.SortBy
import com.example.androidDeviceDetails.viewModel.LocationViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsDisplay.HorizontalPosition.RIGHT
import org.osmdroid.views.CustomZoomButtonsDisplay.VerticalPosition.CENTER
import org.osmdroid.views.MapView.getTileSystem
import java.util.*
import kotlin.collections.ArrayList
import org.osmdroid.config.Configuration as osmConfig


class LocationActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener {
    private lateinit var activityController: ActivityController<LocationData>
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var binding: ActivityLocationBinding

    companion object {
        const val NAME = "LOCATION_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location)
        initRecyclerView()
        initBottomSheet()
        initOnClickListeners()
        initMap()
        activityController = ActivityController(this, NAME, binding)
        locationViewModel = activityController.viewModel as LocationViewModel
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
                UI_MODE_NIGHT_YES -> overlayManager.tilesOverlay.setColorFilter(mapColorFilter())
            }
        }
    }

    private fun mapColorFilter(): ColorMatrixColorFilter {
        val inverseMatrix = ColorMatrix(
            floatArrayOf(
                -1.0f, 0.0f, 0.0f, 0.0f, 255f,
                0.0f, -1.0f, 0.0f, 0.0f, 255f,
                0.0f, 0.0f, -1.0f, 0.0f, 255f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        val destinationColor = Color.parseColor("#FF2A2A2A")
        val lr = (255.0f - Color.red(destinationColor)) / 255.0f
        val lg = (255.0f - Color.green(destinationColor)) / 255.0f
        val lb = (255.0f - Color.blue(destinationColor)) / 255.0f
        val grayscaleMatrix = ColorMatrix(
            floatArrayOf(
                lr, lg, lb, 0F, 0F,
                lr, lg, lb, 0F, 0F,
                lr, lg, lb, 0F, 0F,
                0F, 0F, 0F, 0F, 255F,
            )
        )
        grayscaleMatrix.preConcat(inverseMatrix)
        val dr = Color.red(destinationColor)
        val dg = Color.green(destinationColor)
        val db = Color.blue(destinationColor)
        val drf = dr / 255f
        val dgf = dg / 255f
        val dbf = db / 255f
        val tintMatrix = ColorMatrix(
            floatArrayOf(
                drf, 0F, 0F, 0F, 0F,
                0F, dgf, 0F, 0F, 0F,
                0F, 0F, dbf, 0F, 0F,
                0F, 0F, 0F, 1F, 0F,
            )
        )
        tintMatrix.preConcat(grayscaleMatrix)
        val lDestination = drf * lr + dgf * lg + dbf * lb
        val scale = 1f - lDestination
        val translate = 1 - scale * 0.5f
        val scaleMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0F, 0F, 0F, dr * translate,
                0F, scale, 0F, 0F, dg * translate,
                0F, 0F, scale, 0F, db * translate,
                0F, 0F, 0F, 1F, 0F,
            )
        )
        scaleMatrix.preConcat(tintMatrix)
        return ColorMatrixColorFilter(scaleMatrix)
    }

    private fun initOnClickListeners() {
        binding.apply {
            locationBottomSheet.dateTimePicker.startDate.setOnClickListener(this@LocationActivity)
            locationBottomSheet.dateTimePicker.startTime.setOnClickListener(this@LocationActivity)
            locationBottomSheet.dateTimePicker.endDate.setOnClickListener(this@LocationActivity)
            locationBottomSheet.dateTimePicker.endTime.setOnClickListener(this@LocationActivity)
            locationBottomSheet.sortButton.setOnClickListener(this@LocationActivity)
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.locationBottomSheet.bottomSheet)
        bottomSheetBehavior.peekHeight = 300
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sortButton -> {
                if (binding.locationBottomSheet.sortButton.tag == "down")
                    activityController.sortView(SortBy.DESCENDING.ordinal)
                else
                    activityController.sortView(SortBy.ASCENDING.ordinal)
            }
            R.id.startDate -> activityController.setDate(this, R.id.startDate)
            R.id.startTime -> activityController.setTime(this, R.id.startTime)
            R.id.endDate -> activityController.setDate(this, R.id.endDate)
            R.id.endTime -> activityController.setTime(this, R.id.endTime)
        }
    }

    override fun onItemClicked(clickedItem: LocationData) {
        locationViewModel.focusMapTo(clickedItem.latitude, clickedItem.longitude)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}
