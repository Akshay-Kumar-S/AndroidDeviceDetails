package com.example.androidDeviceDetails.models.signal

data class SignalCookedData(

    var roamingTime: Long = 0,
    var lastCellularStrength: Float = 0F,
    var mostUsedOperator: String = "Unknown",
    var mostUsedCellularBand: String = "Unknown",
) {
    var graphEntryList = arrayListOf<SignalGraphEntry>()
    var mostUsedWifiLevel: String = "Unknown"
    var lastWifiStrength: Float = 0F
    var mostUsedWifi: String = "Unknown"
}

data class SignalGraphEntry(

    val timeStamp: Long,
    val signal: Int,
    val strength: Int,
)