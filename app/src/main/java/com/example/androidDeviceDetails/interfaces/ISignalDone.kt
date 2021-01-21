package com.example.androidDeviceDetails.interfaces

import com.example.androidDeviceDetails.models.signalModels.SignalEntry

interface ISignalDone {
    fun onDone(signalList: ArrayList<SignalEntry>)
}