package com.example.androidDeviceDetails.controllers

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.SignalCookingInterface
import com.example.androidDeviceDetails.SignalStrengthCooker
import com.example.androidDeviceDetails.SignalStrengthViewModel
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.Signal

class SignalController(
    var signalBinding: ActivitySignalStrengthBinding,
    var context: Context,
    var lifecycleOwner: LifecycleOwner
) {
    private var viewModel: SignalStrengthViewModel = SignalStrengthViewModel(signalBinding, context)
    private var db = RoomDB.getDatabase()!!
    private var signal = 0
    lateinit var signalCooker: SignalStrengthCooker
    private val onCookingDone = object : SignalCookingInterface<CellularRaw> {
        override fun updateListView(outputList: ArrayList<CellularRaw>) =
            viewModel.updateListView(outputList)

    }

    fun onCreate() {
        signalCooker = SignalStrengthCooker(signalBinding, context)
        signalCooker.listionDate()
        observeSignal(Signal.CELLULAR.ordinal)
        signalBinding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cellularStrength -> {
                    viewModel.setGuage(-50f,-150f)
                    observeSignal(Signal.CELLULAR.ordinal)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.wifiStrength -> {
                    viewModel.setGuage(0f,-100f)
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

    fun setCooker() {

        signalCooker.cookSignalData(onCookingDone,signalCooker.getFromTimestamp(),signalCooker.getToTimestamp(),signal)
    }
}