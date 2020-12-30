package com.example.androidDeviceDetails.controllers

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.SignalStrengthCooker
import com.example.androidDeviceDetails.SignalViewModel
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.Signal
import com.example.androidDeviceDetails.utils.ListAdaptor

class SignalController(
    var binding: ActivitySignalStrengthBinding,
    var context: Context,
    var lifecycleOwner: LifecycleOwner
) {
    private var viewModel: SignalViewModel = SignalViewModel(binding,context)
    private var db = RoomDB.getDatabase()!!
    private var signal = 0

    lateinit var signalStrengthCooker: SignalStrengthCooker

    fun onCreate() {
        signalStrengthCooker = SignalStrengthCooker(binding, context)
        observeSignal(Signal.CELLULAR.ordinal)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellularStrength -> {
                   binding.gauge.setMaxValue(-50f)
                    binding.gauge.setMinValue(-150f)
                    observeSignal(Signal.CELLULAR.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifiStrength -> {
                    binding.gauge.setMaxValue(0f)
                    binding.gauge.setMinValue(-100f)
                    observeSignal(Signal.WIFI.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }


    fun observeSignal(signal: Int) {
        when (signal) {
            Signal.CELLULAR.ordinal -> {
                this.signal = 0
                observeCellular()
            }
            Signal.WIFI.ordinal -> {
                this.signal = 1
                observeWifi()
            }
        }
    }

    fun observeCellular() {
        db.cellularDao().getLastLive().observe(lifecycleOwner) {
            if (signal == Signal.CELLULAR.ordinal)
                viewModel.updateCellularGauge(it)
        }
    }

    fun observeWifi() {
        db.wifiDao().getLastLive().observe(lifecycleOwner) {
            if (signal == Signal.WIFI.ordinal)
                viewModel.updateWifiGauge(it)
        }
    }
    fun updateListView() {
        signalStrengthCooker.getDbData()

    }
}