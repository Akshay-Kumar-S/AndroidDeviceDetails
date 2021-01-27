package com.example.androidDeviceDetails.models.signal

import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView

data class Chart(
    var aaChartView: AAChartView,
    var title: String,
    var yAxisMin: Float,
    var yAxisMax: Float,
    var color: String,
)