package com.example.androidDeviceDetails.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.fragments.SortBySheet
import com.example.androidDeviceDetails.models.battery.BatteryAppEntry
import com.example.androidDeviceDetails.utils.SortBy
import java.util.*

class BatteryActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityBatteryBinding
    private lateinit var controller: ActivityController<BatteryAppEntry>
    private lateinit var sortBySheet: SortBySheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battery)
        controller = ActivityController(this, NAME, binding)
        sortBySheet = SortBySheet(options, controller::sortView, SortBy.DESCENDING.ordinal)
        binding.apply {
            listView.setOnItemClickListener { parent, _, position, _ ->
                redirectToAppInfo(parent, position)
            }
            pickerBinding.startTime.setOnClickListener(this@BatteryActivity)
            pickerBinding.startDate.setOnClickListener(this@BatteryActivity)
            pickerBinding.endTime.setOnClickListener(this@BatteryActivity)
            pickerBinding.endDate.setOnClickListener(this@BatteryActivity)
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

    private fun redirectToAppInfo(parent: AdapterView<*>, position: Int) {
        val adapter = parent.adapter as BatteryListAdapter
        val item = adapter.getItem(position)
        val infoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
        infoIntent.data = Uri.parse("package:${item?.packageId}")
        startActivity(infoIntent)
    }

    private val options = arrayListOf(
        "Battery Drop (largest first)" to SortBy.DESCENDING.ordinal,
        "Battery Drop (smallest first)" to SortBy.ASCENDING.ordinal,
        "Package Name (A to Z)" to SortBy.ALPHABETICAL.ordinal,
        "Package Name (Z to A)" to SortBy.REVERSE_ALPHABETICAL.ordinal
    )

    companion object {
        const val NAME = "battery"
    }
}

