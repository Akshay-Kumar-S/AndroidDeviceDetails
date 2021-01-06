package com.example.androidDeviceDetails.activities

import android.app.DatePickerDialog
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
import com.example.androidDeviceDetails.controller.AppController
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry
import com.example.androidDeviceDetails.utils.Utils
import java.util.*

class BatteryActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var batteryBinding: ActivityBatteryBinding
    private lateinit var batteryController: AppController<ActivityBatteryBinding, BatteryAppEntry>
    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        batteryBinding = DataBindingUtil.setContentView(this, R.layout.activity_battery)
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        batteryController = AppController(NAME, batteryBinding, this)
        batteryBinding.apply {
            leftArrow.setOnClickListener(this@BatteryActivity)
            rightArrow.setOnClickListener(this@BatteryActivity)
            today.setOnClickListener(this@BatteryActivity)
            batteryListView.setOnItemClickListener { parent, _, position, _ ->
                redirectToAppInfo(parent, position)
            }
        }
        batteryBinding.today.text = Utils.getDateString(calendar)
        batteryController.cook(
            TimeInterval(
                calendar.timeInMillis,
                calendar.timeInMillis + 24 * 60 * 60 * 1000
            )
        )
    }

    private fun redirectToAppInfo(parent: AdapterView<*>, position: Int) {
        val adapter = parent.adapter as BatteryListAdapter
        val item = adapter.getItem(position)
        val infoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
        infoIntent.data = Uri.parse("package:${item?.packageId}")
        startActivity(infoIntent)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.leftArrow -> {
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                batteryBinding.today.text = Utils.getDateString(calendar)
                batteryController.cook(
                    TimeInterval(
                        calendar.timeInMillis,
                        calendar.timeInMillis + 24 * 60 * 60 * 1000
                    )
                )
            }

            R.id.rightArrow -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                batteryBinding.today.text = Utils.getDateString(calendar)
                batteryController.cook(
                    TimeInterval(
                        calendar.timeInMillis,
                        calendar.timeInMillis + 24 * 60 * 60 * 1000
                    )
                )
            }
            R.id.today -> selectDate()
        }
    }

    private fun selectDate() {
        DatePickerDialog(
            this,
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    private var datePickerListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            batteryBinding.today.text = Utils.getDateString(calendar)
            batteryController.cook(
                TimeInterval(
                    calendar.timeInMillis,
                    calendar.timeInMillis + 24 * 60 * 60 * 1000
                )
            )
        }

    companion object {
        const val NAME = "battery"
    }
}

