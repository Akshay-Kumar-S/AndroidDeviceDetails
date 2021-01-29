package com.example.androidDeviceDetails.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.database.AppNetworkUsageRaw
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.fragments.SortBySheet
import com.example.androidDeviceDetails.utils.SortBy
import java.util.*

class NetworkUsageActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAppDataBinding
    private lateinit var controller: ActivityController<AppNetworkUsageRaw>
    private lateinit var sortBySheet: SortBySheet

    companion object {
        const val NAME = "network"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_data)
        controller = ActivityController(
            NAME, binding, this, binding.pickerBinding, supportFragmentManager
        )
        sortBySheet = SortBySheet(options, controller::sortView, SortBy.WIFI_DESCENDING.ordinal)
        binding.apply {
            pickerBinding.startTime.setOnClickListener(this@NetworkUsageActivity)
            pickerBinding.startDate.setOnClickListener(this@NetworkUsageActivity)
            pickerBinding.endTime.setOnClickListener(this@NetworkUsageActivity)
            pickerBinding.endDate.setOnClickListener(this@NetworkUsageActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startTime -> controller.setTime(this, R.id.startTime)
            R.id.startDate -> controller.setDate(this, R.id.startDate)
            R.id.endTime -> controller.setTime(this, R.id.endTime)
            R.id.endDate -> controller.setDate(this, R.id.endDate)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "Sort By") sortBySheet.show(supportFragmentManager, "Sort By")
        return super.onOptionsItemSelected(item)
    }

    private val options = arrayListOf(
        "Wifi Data (largest first)" to SortBy.WIFI_DESCENDING.ordinal,
        "Wifi Data (smallest first)" to SortBy.WIFI_ASCENDING.ordinal,
        "Cellular Data (largest first)" to SortBy.CELLULAR_DESCENDING.ordinal,
        "Cellular Data (smallest first)" to SortBy.CELLULAR_ASCENDING.ordinal,
        "Package Name (A to Z)" to SortBy.ALPHABETICAL.ordinal,
        "Package Name (Z to A)" to SortBy.REVERSE_ALPHABETICAL.ordinal
    )
}