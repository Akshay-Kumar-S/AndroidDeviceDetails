package com.example.androidDeviceDetails.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry
import java.util.*

class BatteryActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var batteryBinding: ActivityBatteryBinding
    private lateinit var batteryController: ActivityController<ActivityBatteryBinding, BatteryAppEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        batteryBinding = DataBindingUtil.setContentView(this, R.layout.activity_battery)
        batteryController = ActivityController(
            NAME,
            batteryBinding,
            this,
            batteryBinding.dateTimePickerLayout,
            supportFragmentManager = supportFragmentManager
        )
        batteryBinding.apply {
            batteryListView.setOnItemClickListener { parent, _, position, _ ->
                redirectToAppInfo(parent, position)
            }
            dateTimePickerLayout.findViewById<TextView>(R.id.startTime)
                .setOnClickListener(this@BatteryActivity)
            dateTimePickerLayout.findViewById<TextView>(R.id.startDate)
                .setOnClickListener(this@BatteryActivity)
            dateTimePickerLayout.findViewById<TextView>(R.id.endTime)
                .setOnClickListener(this@BatteryActivity)
            dateTimePickerLayout.findViewById<TextView>(R.id.endDate)
                .setOnClickListener(this@BatteryActivity)
        }
        batteryController.showInitialData()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startTime -> batteryController.setStartTime(this)
            R.id.startDate -> batteryController.setStartDate(this)
            R.id.endTime -> batteryController.setEndTime(this)
            R.id.endDate -> batteryController.setEndDate(this)
        }
    }

    private fun redirectToAppInfo(parent: AdapterView<*>, position: Int) {
        val adapter = parent.adapter as BatteryListAdapter
        val item = adapter.getItem(position)
        val infoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
        infoIntent.data = Uri.parse("package:${item?.packageId}")
        startActivity(infoIntent)
    }

    companion object {
        const val NAME = "battery"
    }
}

