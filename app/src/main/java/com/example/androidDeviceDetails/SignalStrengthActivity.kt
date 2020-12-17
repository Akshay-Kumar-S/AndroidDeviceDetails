package com.example.androidDeviceDetails

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.WifiRaw

class SignalStrengthActivity : AppCompatActivity() {
    private var db = RoomDB.getDatabase()!!

    private lateinit var binding: ActivitySignalStrengthBinding
    private var cellStrength: Int = -100
    private var wifiStrength: Int = -80
    private var linkspeed: String = "0"
    private var cellInfoType: String = "LTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signal_strength)
        updateGauge()
        db.wifiDao().getLastLive().observe(this) {
            updateWifiGauge(it)
        }
        db.cellularDao().getLastLive().observe(this) {
            updateCellularGauge(it)
        }

    }

    private fun updateGauge() {
        binding.gaugeCellular.moveToValue(cellStrength.toFloat())
        binding.gaugeCellular.setLowerText(cellInfoType)
        binding.gaugeCellular.setUpperText(cellStrength.toString())
        binding.gaugeWifi.moveToValue(wifiStrength.toFloat())
        binding.gaugeWifi.setLowerText(linkspeed)
        binding.gaugeWifi.setUpperText(wifiStrength.toString())
    }

    private fun updateWifiGauge(wifiRaw: WifiRaw) {
        Log.d("test", "updateWifiGauge: ")
        wifiStrength = wifiRaw.strength!!
        linkspeed = wifiRaw.linkSpeed.toString()
        binding.gaugeWifi.moveToValue(wifiStrength.toFloat())
        binding.gaugeWifi.setLowerText(linkspeed)
        binding.gaugeWifi.setUpperText(wifiStrength.toString())
    }

    private fun updateCellularGauge(cellularRaw: CellularRaw) {
        Log.d("tag", "updateCellularGauge: ")
        cellStrength = cellularRaw.strength!!
        cellInfoType = cellularRaw.type.toString()
        binding.gaugeCellular.moveToValue(cellStrength.toFloat())
        binding.gaugeCellular.setLowerText(cellInfoType)
        binding.gaugeCellular.setUpperText(cellStrength.toString())
    }

}