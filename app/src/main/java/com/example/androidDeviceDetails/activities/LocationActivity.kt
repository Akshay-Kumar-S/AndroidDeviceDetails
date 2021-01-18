package com.example.androidDeviceDetails.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.interfaces.OnItemClickListener
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


class LocationActivity : AppCompatActivity(), View.OnClickListener,OnItemClickListener {
    private lateinit var activityController: ActivityController<LocationModel>
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
        activityController = ActivityController(NAME, binding, this,
            binding.bottomLocation.dateTimePickerLayout, supportFragmentManager)
        locationViewModel = activityController.viewModel as LocationViewModel
        initDatePicker()
        initMap()
        binding.apply {
            bottomLocation.countView.setOnClickListener(this@LocationActivity)
        }
    }

    private fun initRecyclerView() {
        val arrayList = ArrayList<CountModel>()
        arrayList.add(CountModel("NoData", 0, ""))
        binding.bottomLocation.locationListView.adapter = LocationAdapter(arrayList,this)
        binding.bottomLocation.locationListView.isNestedScrollingEnabled=true
    }

    private fun initMap() {
        Configuration.getInstance().load(
            applicationContext, getSharedPreferences(
                "my.app.packagename_preferences", Context.MODE_PRIVATE
            )
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
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

    override fun onItemClicked(clickedItem: CountModel) {
        locationViewModel.focusOnMap(clickedItem.geoHash)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}
