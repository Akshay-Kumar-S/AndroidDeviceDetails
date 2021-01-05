package com.example.androidDeviceDetails.controllers

import android.content.Context
import com.example.androidDeviceDetails.SignalViewModel
import com.example.androidDeviceDetails.cooker.SignalCooker
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.Signal
import com.example.androidDeviceDetails.models.SignalRaw
import com.example.androidDeviceDetails.models.TimeInterval

class Controller(
    var signalBinding: ActivitySignalStrengthBinding,
    var context: Context,
) {
    private var viewModel: SignalViewModel = SignalViewModel(signalBinding, context)
    var signalCooker = SignalCooker()

    fun cook(signal: Int, timeInterval: TimeInterval) {
        if (signal == Signal.WIFI.ordinal)
            signalCooker.cook(timeInterval, onCookingDone)
        else
            signalCooker.get(timeInterval,onCookingDone)
    }

    private val onCookingDone = object : ICookingDone<SignalRaw> {
        override fun onDone(outputList: ArrayList<SignalRaw>) =
            viewModel.updateListView(outputList)
    }

}