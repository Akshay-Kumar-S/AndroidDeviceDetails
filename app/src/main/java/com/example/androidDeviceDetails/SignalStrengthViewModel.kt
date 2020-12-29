package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.WifiRaw
import com.example.androidDeviceDetails.utils.ListAdaptor

class SignalStrengthViewModel(
    private val signalStrengthBinding: ActivitySignalStrengthBinding,
    val context: Context
) {
    private var strength: Int = -100
    private var linkspeed: String = "0"
    private var cellInfoType: String = "LTE"
    private var signal = 0

    @SuppressLint("SetTextI18n")
     fun updateWifiGauge(wifiRaw: WifiRaw) {
        Log.d("test", "updateWifiGauge: ")
        strength = wifiRaw.strength!!
        linkspeed = wifiRaw.linkSpeed.toString()
        signalStrengthBinding.gauge.moveToValue(strength.toFloat())
        signalStrengthBinding.gauge.setLowerText(strength.toString())
        signalStrengthBinding.textStrength.text = "${strength.toString()} dBm"
        signalStrengthBinding.textView3.text = "Linkspeed"
        signalStrengthBinding.textView4.text = "$linkspeed MHz"
    }

    @SuppressLint("SetTextI18n")
     fun updateCellularGauge(cellularRaw: CellularRaw) {
        Log.d("tag", "updateCellularGauge: ")
        strength = cellularRaw.strength!!
        cellInfoType = cellularRaw.type.toString()
        signalStrengthBinding.gauge.moveToValue(strength.toFloat())
        signalStrengthBinding.gauge.setLowerText(strength.toString())
        signalStrengthBinding.textStrength.text = "${strength.toString()} dBm"
        signalStrengthBinding.textView3.text = "Type"
        signalStrengthBinding.textView4.text = cellInfoType
    }

    fun updateListView(cellularList: List<CellularRaw>) {


        val adapter = ListAdaptor(context, R.layout.signal_tile, cellularList)
        signalStrengthBinding.listView.adapter = adapter

    }
}
