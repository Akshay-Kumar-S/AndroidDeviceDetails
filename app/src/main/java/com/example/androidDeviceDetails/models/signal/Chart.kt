package com.example.androidDeviceDetails.models.signal

data class Chart(
    var id: Int,
    var title: String,
    var yAxisMin: Float,
    var yAxisMax: Float,
    var color: String,
)
