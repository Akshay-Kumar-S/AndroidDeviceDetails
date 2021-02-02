package com.example.androidDeviceDetails.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.database.AppNetworkUsageRaw
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.fragments.SortBySheet
import com.example.androidDeviceDetails.utils.DateTimePicker
import com.example.androidDeviceDetails.utils.SortBy

class NetworkUsageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppDataBinding
    private lateinit var controller: ActivityController<AppNetworkUsageRaw>
    private lateinit var sortBySheet: SortBySheet

    companion object {
        const val NAME = "network"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_data)
        controller = ActivityController(this, NAME, binding)
        sortBySheet = SortBySheet(options, controller::sortView, SortBy.WIFI_DESCENDING.ordinal)
        DateTimePicker(this, binding.pickerBinding, controller::setTime, controller::setDate)
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