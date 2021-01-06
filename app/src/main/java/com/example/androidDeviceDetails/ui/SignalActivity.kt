package com.example.androidDeviceDetails.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.utils.SignalUtil
import com.example.androidDeviceDetails.controller.AppController
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.SignalRaw
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.viewModel.SignalViewModel

class SignalActivity : AppCompatActivity() {
    private lateinit var signalBinding: ActivitySignalStrengthBinding
    private lateinit var viewModel: SignalViewModel
    private lateinit var wifiController: AppController<ActivitySignalStrengthBinding, SignalRaw>
    private lateinit var cellularController: AppController<ActivitySignalStrengthBinding, SignalRaw>
    private lateinit var signalUtil: SignalUtil
    private var displayList: Int = 0
    private var startTime: Long = 0
    private var endTime: Long = 0

    companion object {
        const val WIFI = "wifi"
        const val CELLULAR = "cellular"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        signalBinding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        viewModel = SignalViewModel(signalBinding, this)
        signalUtil = SignalUtil(signalBinding, this)
        wifiController = AppController(WIFI, signalBinding, this)
        cellularController = AppController(CELLULAR, signalBinding, this)

        viewModel.observeSignal(lifecycleOwner = this)
        signalBinding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellular -> {
                    viewModel.updateGauge(-50f, -150f)
                    if (displayList == 1)
                       cellularController.cook(TimeInterval(startTime, endTime))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifi -> {
                    viewModel.updateGauge(0f, -100f)
                    if (displayList == 1)
                        wifiController.cook(TimeInterval(startTime, endTime))
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        signalUtil.onCreate()
        signalBinding.filter.setOnClickListener()
        {
            displayList = 1
            signalUtil.onCreate()
            startTime = signalUtil.getStartTimestamp()
            endTime = signalUtil.getEndTimestamp()
            Log.d("neena", "onCreate: $startTime $endTime")
            cellularController.cook(TimeInterval(startTime, endTime))
        }
    }
}