package com.example.androidDeviceDetails.models

data class SignalRaw(
    var timeStamp: Long,
    var strength: Int?,
    var general: String?,
    var level: Int?
)