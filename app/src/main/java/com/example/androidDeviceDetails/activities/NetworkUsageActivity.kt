package com.example.androidDeviceDetails.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageRaw
import java.util.*

class NetworkUsageActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAppDataBinding
    private lateinit var networkUsageController: ActivityController<AppNetworkUsageRaw>

    companion object {
        const val NAME = "network"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_data)
        networkUsageController = ActivityController(
            NAME, binding, this, binding.pickerBinding, supportFragmentManager
        )
        binding.apply {
            pickerBinding.startTime.setOnClickListener(this@NetworkUsageActivity)
            pickerBinding.startDate.setOnClickListener(this@NetworkUsageActivity)
            pickerBinding.endTime.setOnClickListener(this@NetworkUsageActivity)
            pickerBinding.endDate.setOnClickListener(this@NetworkUsageActivity)
        }
        networkUsageController.showInitialData()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startTime -> networkUsageController.setStartTime(this)
            R.id.startDate -> networkUsageController.setStartDate(this)
            R.id.endTime -> networkUsageController.setEndTime(this)
            R.id.endDate -> networkUsageController.setEndDate(this)
        }
    }

}