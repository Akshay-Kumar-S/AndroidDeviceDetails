package com.example.androidDeviceDetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.signalModels.Chart
import com.example.androidDeviceDetails.models.signalModels.SignalEntry
import com.example.androidDeviceDetails.utils.Signal
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.google.gson.Gson

class GraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gson = Gson()
        val sig = intent.getStringExtra("key")
        val signalList = gson.fromJson(sig, Array<SignalEntry>::class.java)
        var size = signalList.size
        var wifiSize = 0
        var cellularSize = 0

        size -= 1
        for (i in 0..size) {
            if (signalList[i].signal == Signal.CELLULAR.ordinal)
                cellularSize += 1
            else if (signalList[i].signal == Signal.WIFI.ordinal)
                wifiSize += 1
        }

        val cellularTimeList = Array(cellularSize) { "" }
        val cellularValueList = Array<Any>(cellularSize) {}
        val wifiValueList = Array<Any>(wifiSize) {}
        val wifiTimeList = Array(wifiSize) { "" }

        var c = 0
        var w = 0
        for (i in 0..size) {
            if (signalList[i].signal == Signal.CELLULAR.ordinal) {
                cellularTimeList[c] = signalList[i].timeStamp
                cellularValueList[c] = signalList[i].strength
                c += 1
            } else if (signalList[i].signal == Signal.WIFI.ordinal) {
                wifiTimeList[w] = signalList[i].timeStamp
                wifiValueList[w] = signalList[i].strength
                w += 1
            }
        }

        setContentView(R.layout.activity_graph)

        val wifiChart = Chart(
            R.id.wifi_chart, "WIFI", -127f, 0f, "#ffc069"
        )
        drawChart(wifiChart, wifiTimeList, wifiValueList)
        val cellularChart = Chart(
            R.id.cellular_chart, "CELLULAR", -150f, -50f, "#06caf4"
        )
        drawChart(cellularChart, cellularTimeList, cellularValueList)
    }

    private fun drawChart(chart: Chart, xSet: Array<String>, ySet: Array<Any>) {
        val aaChartView = findViewById<AAChartView>(chart.id)
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Spline)
            .title(chart.title)
            .categories(xSet)
            .yAxisLabelsEnabled(true)
            .yAxisGridLineWidth(0f)
            .xAxisLabelsEnabled(false)
            .touchEventEnabled(true)
            .yAxisMin(chart.yAxisMin)
            .yAxisMax(chart.yAxisMax)
            .yAxisTitle("strength")
            .tooltipValueSuffix("dBm")
            .colorsTheme(arrayOf(chart.color))
            .legendEnabled(false)
            .zoomType(AAChartZoomType.XY)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name(chart.title)
                        .data(ySet),
                )
            )

        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }
}