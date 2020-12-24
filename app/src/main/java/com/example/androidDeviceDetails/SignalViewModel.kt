package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.util.Log
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.WifiRaw

class SignalViewModel(private val binding: ActivitySignalStrengthBinding) {
    private var strength: Int = 0
    private var cellInfoType: String = "LTE"
    private var linkspeed: String = "0"

    @SuppressLint("SetTextI18n")
    fun updateCellularGauge(cellularRaw: CellularRaw) {
        Log.d("tag", "updateCellularGauge: ")
        strength = cellularRaw.strength!!
        cellInfoType = cellularRaw.type.toString()
        binding.gauge.moveToValue(strength.toFloat())
        binding.gauge.setLowerText(strength.toString())
        binding.textStrength.text = "$strength dBm"
        binding.textView3.text = "Type"
        binding.textView4.text = cellInfoType
    }

    @SuppressLint("SetTextI18n")
    fun updateWifiGauge(wifiRaw: WifiRaw) {
        Log.d("test", "updateWifiGauge: ")
        strength = wifiRaw.strength!!
        linkspeed = wifiRaw.linkSpeed.toString()
        binding.gauge.moveToValue(strength.toFloat())
        binding.gauge.setLowerText(strength.toString())
        binding.textStrength.text = "$strength dBm"
        binding.textView3.text = "Linkspeed"
        binding.textView4.text = "$linkspeed MHz"
    }

}