package com.example.androidDeviceDetails.controllers

import android.content.Context
import com.example.androidDeviceDetails.SignalViewModel
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.RoomDB

class SignalController(binding: ActivitySignalStrengthBinding,var context: Context) {
    private var viewModel: SignalViewModel =SignalViewModel(binding)
    private var db = RoomDB.getDatabase()!!

    fun observeCellular(signal:Int){
        db.cellularDao().getLastLive().observe(this) {
            if (signal == 0)
                viewModel.updateCellularGauge(it)
        }
    }

    fun observeWifi(signal:Int){
        db.wifiDao().getLastLive().observe(this) {
            if (signal == 1)
                viewModel.updateWifiGauge(it)
        }
    }



}
