package com.example.androidDeviceDetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.databinding.ActivitySignalGraphBinding
import com.example.androidDeviceDetails.models.signal.Chart
import com.example.androidDeviceDetails.models.signal.Signal
import com.example.androidDeviceDetails.models.signal.SignalGraphEntry
import com.example.androidDeviceDetails.utils.Utils
import com.google.gson.Gson

class SignalGraphActivity : AppCompatActivity() {
    private lateinit var signalGraphBinding: ActivitySignalGraphBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signalGraphBinding = DataBindingUtil.setContentView(this, R.layout.activity_signal_graph)

        val signal = intent.getStringExtra("signal")
        val graphEntryList = Gson().fromJson(signal, Array<SignalGraphEntry>::class.java)

        val cellularTimeList = arrayListOf<String>()
        val cellularValueList = arrayListOf<Int>()
        val wifiValueList = arrayListOf<Int>()
        val wifiTimeList = arrayListOf<String>()

        graphEntryList.partition { it.signal == Signal.CELLULAR }.apply {
            first.forEach {
                cellularTimeList.add(Utils.getDateTime(it.timeStamp))
                cellularValueList.add(it.strength)
            }
            second.forEach {
                wifiTimeList.add(Utils.getDateTime(it.timeStamp))
                wifiValueList.add(it.strength)
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