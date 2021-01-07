package com.example.androidDeviceDetails.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import com.example.androidDeviceDetails.utils.Signal

class SignalActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var signalBinding: ActivitySignalStrengthBinding
    private lateinit var signalController: ActivityController<ActivitySignalStrengthBinding, SignalEntity>

    companion object {
        const val NAME = "signal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        signalBinding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        signalController = ActivityController(
            NAME,
            signalBinding,
            this,
            signalBinding.datePicker,
            supportFragmentManager
        )
//        signalBinding.bottomNavigationView.menu.findItem(R.id.cellular).isChecked = true
        signalBinding.apply {
            datePicker.findViewById<TextView>(R.id.startTime)
                .setOnClickListener(this@SignalActivity)
            datePicker.findViewById<TextView>(R.id.startDate)
                .setOnClickListener(this@SignalActivity)
            datePicker.findViewById<TextView>(R.id.endTime)
                .setOnClickListener(this@SignalActivity)
            datePicker.findViewById<TextView>(R.id.endDate)
                .setOnClickListener(this@SignalActivity)
        }

        signalBinding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellular -> {
                    signalController.filterData(Signal.CELLULAR.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifi -> {
                    signalController.filterData(Signal.WIFI.ordinal)
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