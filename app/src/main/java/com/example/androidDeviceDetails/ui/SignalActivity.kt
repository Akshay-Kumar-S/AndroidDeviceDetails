package com.example.androidDeviceDetails.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import com.example.androidDeviceDetails.utils.Signal

class SignalActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var signalBinding: ActivitySignalStrengthBinding
    private lateinit var signalController: ActivityController<SignalEntity>

    companion object {
        const val NAME = "signal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        signalBinding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        signalBinding.lifecycleOwner = this
        signalController = ActivityController(
            NAME,
            signalBinding,
            this,
            signalBinding.dateTimePickerLayout,
            supportFragmentManager
        )
//        signalBinding.bottomNavigationView.menu.findItem(R.id.cellular).isChecked = true
        signalBinding.apply {
            dateTimePickerLayout.startTime
                .setOnClickListener(this@SignalActivity)
            dateTimePickerLayout.startDate
                .setOnClickListener(this@SignalActivity)
            dateTimePickerLayout.endTime
                .setOnClickListener(this@SignalActivity)
            dateTimePickerLayout.endDate
                .setOnClickListener(this@SignalActivity)
        }
        signalBinding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellular -> {
                    signalController.filterView(Signal.CELLULAR.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifi -> {
                    signalController.filterView(Signal.WIFI.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startTime -> signalController.setStartTime(this)
            R.id.startDate -> signalController.setStartDate(this)
            R.id.endTime -> signalController.setEndTime(this)
            R.id.endDate -> signalController.setEndDate(this)
        }
    }

}