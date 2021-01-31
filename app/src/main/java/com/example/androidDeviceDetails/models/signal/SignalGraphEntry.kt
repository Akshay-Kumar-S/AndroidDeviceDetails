package com.example.androidDeviceDetails.models.signal

data class SignalGraphEntry(
    val timeStamp: Long,
    val signal: Int,
    val strength: Int,
)