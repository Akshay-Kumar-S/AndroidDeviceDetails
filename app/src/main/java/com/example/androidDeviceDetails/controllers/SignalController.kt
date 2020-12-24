package com.example.androidDeviceDetails.controllers

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.example.androidDeviceDetails.SignalViewModel
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.Signal

class SignalController(
    binding: ActivitySignalStrengthBinding,
    context: Context,
    var lifecycleOwner: LifecycleOwner
) {
    private var viewModel: SignalViewModel = SignalViewModel(binding)
    private var db = RoomDB.getDatabase()!!
    private var signal = 0

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

}