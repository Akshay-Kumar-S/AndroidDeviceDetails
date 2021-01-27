package com.example.androidDeviceDetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.databinding.ActivitySignalGraphBinding
import com.example.androidDeviceDetails.models.signal.Chart
import com.example.androidDeviceDetails.models.signal.SignalEntry
import com.example.androidDeviceDetails.utils.Signal
import com.example.androidDeviceDetails.utils.Utils
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.google.gson.Gson

class SignalGraphActivity : AppCompatActivity() {
    private lateinit var signalGraphBinding: ActivitySignalGraphBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signalGraphBinding = DataBindingUtil.setContentView(this, R.layout.activity_signal_graph)

        val signal = intent.getStringExtra("signal")
        val signalEntryList = Gson().fromJson(signal, Array<SignalEntry>::class.java)

        val cellularTimeList = arrayListOf<String>()
        val cellularValueList = arrayListOf<Int>()
        val wifiValueList = arrayListOf<Int>()
        val wifiTimeList = arrayListOf<String>()

        for (signalEntry in signalEntryList) {
            when (signalEntry.signal) {
                Signal.CELLULAR.ordinal -> {
                    cellularTimeList.add(signalEntry.timeStamp)
                    cellularValueList.add(signalEntry.strength)
                }
                Signal.WIFI.ordinal -> {
                    wifiTimeList.add(signalEntry.timeStamp)
                    wifiValueList.add(signalEntry.strength)
                }
            }
        }

        val wifiChart = Chart(
            aaChartView = signalGraphBinding.wifiChart,
            title = "WIFI",
            yAxisMin = -127f,
            yAxisMax = 0f,
            color = "#ffc069"
        )
        Utils.drawChart(wifiChart, wifiTimeList.toTypedArray(), wifiValueList.toArray())

        val cellularChart = Chart(
            aaChartView = signalGraphBinding.cellularChart,
            title = "CELLULAR",
            yAxisMin = -150f,
            yAxisMax = -50f,
            color = "#06caf4"
        )
        Utils.drawChart(cellularChart, cellularTimeList.toTypedArray(), cellularValueList.toArray())
    }
}