package com.example.androidDeviceDetails.models.signalModels

data class SignalEntry(
    var timeStamp: Long,
    var strength: Int?,
    var general: String?,
    var level: Int?
)