package com.example.androidDeviceDetails.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry
import java.util.*

class BatteryActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityBatteryBinding
    private lateinit var controller: ActivityController<BatteryAppEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battery)
        controller = ActivityController(
            NAME, binding, this, binding.pickerBinding, supportFragmentManager
        )
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

