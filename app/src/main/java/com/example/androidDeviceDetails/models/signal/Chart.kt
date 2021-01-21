package com.example.androidDeviceDetails.models.signalModels

data class Chart(
    var id: Int,
    var title: String,
    var yAxisMin: Float,
    var yAxisMax: Float,
    var color: String,
)
