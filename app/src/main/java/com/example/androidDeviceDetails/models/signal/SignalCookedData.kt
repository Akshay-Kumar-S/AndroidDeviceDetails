package com.example.androidDeviceDetails.models.signal

data class SignalCookedData(
    var roamingTime: String = "0",
    var mostUsedOperator: String = "Unknown",
    var mostUsedWifi: String = "Unknown",
    var mostUsedCellularBand: String = "Unknown",
    var mostUsedWifiLevel: String = "Unknown",
    var lastWifiStrength: Float = 0F,
    var lastCellularStrength: Float = 0F,
)