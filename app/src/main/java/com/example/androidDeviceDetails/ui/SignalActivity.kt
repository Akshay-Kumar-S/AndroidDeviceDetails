package com.example.androidDeviceDetails.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.utils.SignalUtil
import com.example.androidDeviceDetails.controller.AppController
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.signalModels.SignalEntry
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import com.example.androidDeviceDetails.viewModel.SignalViewModel

class SignalActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var signalBinding: ActivitySignalStrengthBinding
    private lateinit var viewModel: SignalViewModel
    private lateinit var signalController: AppController<ActivitySignalStrengthBinding, SignalEntity>
    private lateinit var signalUtil: SignalUtil
    private var startTime: Long = 0
    private var endTime: Long = 0

    companion object {
        const val NAME = "signal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        signalBinding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        viewModel = SignalViewModel(signalBinding, this)
        signalUtil = SignalUtil(signalBinding, this)
        signalController = AppController(NAME, signalBinding, this)

//        signalBinding.bottomNavigationView.menu.findItem(R.id.cellular).isChecked = true
        viewModel.updateCard()

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
        signalBinding.bottomNavigationView.menu.findItem(R.id.cellular).isChecked = true
        viewModel.observeSignal(lifecycleOwner = this)
        signalBinding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellular -> {
                    updateGauge(-50f, -150f)
                    viewModel.updateCard()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifi -> {
                    updateGauge(0f, -100f)
                    viewModel.updateCard()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        signalUtil.onCreate()

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startTime -> cellularController.setStartTime(this)
            R.id.startDate -> cellularController.setStartDate(this)
            R.id.endTime -> cellularController.setEndTime(this)
            R.id.endDate -> cellularController.setEndDate(this)
        }
    }

    private fun updateGauge(max: Float, min: Float) {
        signalBinding.gauge.setMaxValue(max)
        signalBinding.gauge.setMinValue(min)
    }
}