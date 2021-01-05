package com.example.androidDeviceDetails.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.MainController
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.SignalUtil
import com.example.androidDeviceDetails.controller.AppController
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.Signal
import com.example.androidDeviceDetails.models.SignalRaw
import com.example.androidDeviceDetails.models.TimeInterval
import com.example.androidDeviceDetails.viewModel.SignalViewModel

class SignalActivity : AppCompatActivity() {
    private lateinit var signalBinding: ActivitySignalStrengthBinding
    private lateinit var viewModel: SignalViewModel
    private lateinit var signalController: AppController<ActivitySignalStrengthBinding, SignalRaw>
    private var mainController: MainController = MainController()
    private var signal = Signal.CELLULAR.ordinal
    private lateinit var signalUtil: SignalUtil
    private var displayList: Int = 0
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
        mainController.observeSignal(Signal.CELLULAR.ordinal, viewModel, lifecycleOwner = this)
        signalUtil = SignalUtil(signalBinding, this)
        signalController = AppController(SignalActivity.NAME, signalBinding, this)


        signalBinding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellularStrength -> {
                    signal = Signal.CELLULAR.ordinal
                    viewModel.updateGauge(-50f, -150f, signal)
                    if (displayList == 1)
                        signalController.cook(TimeInterval(startTime, endTime))
                    mainController.observeSignal(
                        Signal.CELLULAR.ordinal,
                        viewModel,
                        lifecycleOwner = this
                    )
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifiStrength -> {
                    signal = Signal.WIFI.ordinal
                    viewModel.updateGauge(0f, -100f, signal)
                    if (displayList == 1)
                        signalController.cook(TimeInterval(startTime, endTime))
                    mainController.observeSignal(
                        Signal.WIFI.ordinal,
                        viewModel,
                        lifecycleOwner = this
                    )
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
            signalController.cook(TimeInterval(startTime, endTime))
        }
    }
}