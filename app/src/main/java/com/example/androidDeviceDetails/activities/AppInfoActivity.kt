package com.example.androidDeviceDetails.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.managers.AppInfoManager
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData
import com.example.androidDeviceDetails.models.appInfoModels.EventType
import com.example.androidDeviceDetails.utils.Utils
import java.util.*


class AppInfoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAppInfoBinding
    private var startTime: Long = 0
    private var endTime: Long = 0
    private lateinit var controller: ActivityController<ActivityAppInfoBinding, AppInfoCookedData>

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
        when (item.itemId) {
            R.id.spinner_all -> {
                title.text = "All"
                binding.statisticsContainer.tag = "${EventType.ALL_EVENTS.ordinal}"
            }
            R.id.spinner_enrolled -> {
                title.text = "Enrolled"
                binding.statisticsContainer.tag = "${EventType.APP_ENROLL.ordinal}"

            }
            R.id.spinner_installed -> {
                title.text = "Installed"
                binding.statisticsContainer.tag = "${EventType.APP_INSTALLED.ordinal}"
            }
            R.id.spinner_updated -> {
                title.text = "Updated"
                binding.statisticsContainer.tag = "${EventType.APP_UPDATED.ordinal}"
            }
            R.id.spinner_uninstalled -> {
                title.text = "Uninstalled"
                binding.statisticsContainer.tag = "${EventType.APP_UNINSTALLED.ordinal}"
            }
            R.id.filter_text -> {
            }
            else -> super.onSupportNavigateUp()
        }
        if (startTime != 0L && endTime != 0L) {
            controller.filterData()
        }
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
        binding.appInfoListView.isEnabled = false
        startTime = Utils.loadPreviousDayTime()
        endTime = System.currentTimeMillis()
        cook(TimeInterval(startTime, endTime))
        binding.apply {
            dateTimePickerLayout.findViewById<TextView>(R.id.startTime)
                .setOnClickListener(this@AppInfoActivity)
            dateTimePickerLayout.findViewById<TextView>(R.id.startDate)
                .setOnClickListener(this@AppInfoActivity)
            dateTimePickerLayout.findViewById<TextView>(R.id.endTime)
                .setOnClickListener(this@AppInfoActivity)
            dateTimePickerLayout.findViewById<TextView>(R.id.endDate)
                .setOnClickListener(this@AppInfoActivity)
        }
    }

    fun deleteApp(view: View) {
        AppInfoManager.deleteApp(view, packageManager, this)
    }

    private fun cook(timeInterval: TimeInterval) {
        controller.showInitialData()
        controller.cook(timeInterval)
        binding.indeterminateBar.isVisible = true
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startTime -> controller.setStartTime(this)
            R.id.startDate -> controller.setStartDate(this)
            R.id.endTime -> controller.setEndTime(this)
            R.id.endDate -> controller.setEndDate(this)
        }
    }

}