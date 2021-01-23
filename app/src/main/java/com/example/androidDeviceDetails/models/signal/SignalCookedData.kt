package com.example.androidDeviceDetails.models.signalModels

data class SignalCookedData(
    var roamingTime:Long,
    var mostUsedOperator:String?,
    var mostUsedWifi:String?,
    var mostUsedLevel:String,
    var mostUsedWifiLevel:String,
    var lastWifiStrength:Float,
    var lastCellularStrength:Float,
)
