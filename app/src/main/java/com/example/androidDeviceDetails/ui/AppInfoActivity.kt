package com.example.androidDeviceDetails.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.models.appInfo.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfo.EventType
import com.example.androidDeviceDetails.utils.Utils
import java.util.*

class AppInfoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAppInfoBinding
    private lateinit var controller: ActivityController<AppInfoCookedData>

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_info_menu, menu)
        return true
    }

    companion object {
        const val NAME = "appInfo"
    }

    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val title = findViewById<TextView>(R.id.filter_text)
        var filter = 0
        when (item.itemId) {
            R.id.spinner_all -> {
                title.text = "All"
                filter = EventType.ALL_EVENTS.ordinal
            }
            R.id.spinner_enrolled -> {
                title.text = "Enrolled"
                filter = EventType.APP_ENROLL.ordinal

            }
            R.id.spinner_installed -> {
                title.text = "Installed"
                filter = EventType.APP_INSTALLED.ordinal
            }
            R.id.spinner_updated -> {
                title.text = "Updated"
                filter = EventType.APP_UPDATED.ordinal
            }
            R.id.spinner_uninstalled -> {
                title.text = "Uninstalled"
                filter = EventType.APP_UNINSTALLED.ordinal
            }
            R.id.filter_text -> {
            }
            else -> super.onSupportNavigateUp()
        }
        controller.filterView(filter)

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_info)
        controller = ActivityController(
            NAME,
            binding,
            this,
            binding.dateTimePickerLayout,
            supportFragmentManager
        )
        binding.appInfoListView.isEnabled = true
        binding.apply {
            dateTimePickerLayout.startTime
                .setOnClickListener(this@AppInfoActivity)
            dateTimePickerLayout.startDate
                .setOnClickListener(this@AppInfoActivity)
            dateTimePickerLayout.endTime
                .setOnClickListener(this@AppInfoActivity)
            dateTimePickerLayout.endDate
                .setOnClickListener(this@AppInfoActivity)
        }
    }

    fun deleteApp(view: View) {
        Utils.uninstallApp(view.tag as String, packageManager, this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startTime -> controller.setTime(this, R.id.startTime)
            R.id.startDate -> controller.setDate(this, R.id.startDate)
            R.id.endTime -> controller.setTime(this, R.id.endTime)
            R.id.endDate -> controller.setDate(this, R.id.endDate)
        }
    }
}