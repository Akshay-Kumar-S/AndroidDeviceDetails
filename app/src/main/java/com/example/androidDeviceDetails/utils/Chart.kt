package com.example.androidDeviceDetails.utils

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView

data class Chart(
    var chartView: AAChartView,
    var title: String,
    var yAxisMin: Float,
    var yAxisMax: Float,
    var color: String,
)