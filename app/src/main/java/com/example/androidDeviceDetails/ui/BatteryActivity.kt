package com.example.androidDeviceDetails.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.fragments.SortBySheet
import com.example.androidDeviceDetails.models.battery.BatteryAppEntry
import com.example.androidDeviceDetails.utils.DateTimePicker
import com.example.androidDeviceDetails.utils.SortBy

class BatteryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBatteryBinding
    private lateinit var controller: ActivityController<BatteryAppEntry>
    private lateinit var sortBySheet: SortBySheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battery)
        controller = ActivityController(this, NAME, binding)
        val options = arrayListOf(
            getString(R.string.battery_DES) to SortBy.DESCENDING.ordinal,
            getString(R.string.battery_ASC) to SortBy.ASCENDING.ordinal,
            getString(R.string.battery_PkgName_ALPH) to SortBy.ALPHABETICAL.ordinal,
            getString(R.string.battery_PkgName_REV_APLH) to SortBy.REVERSE_ALPHABETICAL.ordinal
        )
        sortBySheet = SortBySheet(options, controller::sortView, SortBy.DESCENDING.ordinal)
        DateTimePicker(this, binding.pickerBinding, controller::setTime, controller::setDate)
        binding.listView.setOnItemClickListener { parent, _, position, _ ->
            redirectToAppInfo(parent, position)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == getString(R.string.sort_by))
            sortBySheet.show(supportFragmentManager, getString(R.string.sort_by))
        return super.onOptionsItemSelected(item)
    }

    private fun redirectToAppInfo(parent: AdapterView<*>, position: Int) {
        val item = (parent.adapter as BatteryListAdapter).getItem(position)
        val infoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
        infoIntent.data = Uri.parse("package:${item?.packageId}")
        startActivity(infoIntent)
    }

    companion object {
        const val NAME = "battery"
    }
}

