package com.example.androidDeviceDetails.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.*
import com.example.androidDeviceDetails.adapters.SignalAdapter

class SignalViewModel(
    private val signalStrengthBinding: ActivitySignalStrengthBinding,
    val context: Context
) {
    private var signal = Signal.CELLULAR.ordinal
    private var strength: Int = -100
    private var linkspeed: String = "0"
    private var cellInfoType: String = "LTE"

    @SuppressLint("SetTextI18n")
    fun updateWifiGauge(wifiRaw: WifiRaw) {
        Log.d("test", "updateWifiGauge: ")
        strength = wifiRaw.strength!!
        linkspeed = wifiRaw.linkSpeed.toString()
        signalStrengthBinding.gauge.moveToValue(strength.toFloat())
        //signalStrengthBinding.gauge.setLowerText(strength.toString())
        signalStrengthBinding.textStrength.text = "$strength dBm"
        signalStrengthBinding.signalText.text = "LinkSpeed"
        signalStrengthBinding.signalValue.text = "$linkspeed MHz"
    }

    @SuppressLint("SetTextI18n")
    fun updateCellularGauge(cellularRaw: CellularRaw) {
        Log.d("tag", "updateCellularGauge: ")
        strength = cellularRaw.strength!!
        cellInfoType = cellularRaw.type.toString()
        signalStrengthBinding.gauge.moveToValue(strength.toFloat())
        //signalStrengthBinding.gauge.setLowerText(strength.toString())
        signalStrengthBinding.textStrength.text = "$strength dBm"
        signalStrengthBinding.signalText.text = "Type"
        signalStrengthBinding.signalValue.text = cellInfoType
    }

    fun updateGauge(max: Float, min: Float, ordinal: Int) {
        signalStrengthBinding.gauge.setMaxValue(max)
        signalStrengthBinding.gauge.setMinValue(min)
        signal = ordinal
    }

    fun updateListView(signalList: ArrayList<SignalRaw>) {
        Log.d("neena", "updateListView: $signalList")
        if (signalList.isNotEmpty()) {
            signalStrengthBinding.root.post {
                displayList()
                val adapter = SignalAdapter(context, R.layout.signal_tile, signalList)
                signalStrengthBinding.listView.adapter = adapter
            }
        } else
            signalStrengthBinding.root.post {
                signalStrengthBinding.display.isVisible=true
                signalStrengthBinding.list.isVisible = false
            }
    }

    @SuppressLint("SetTextI18n")
    fun displayList() {
        signalStrengthBinding.display.isVisible=false
        signalStrengthBinding.list.isVisible = true
        if (signal == Signal.CELLULAR.ordinal)
            signalStrengthBinding.general.text = "Type"
        else
            signalStrengthBinding.general.text = "Linkspeed"
    }

}