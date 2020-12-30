package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.interfaces.ISignalCookedData
import com.example.androidDeviceDetails.models.RoomDB
import java.lang.reflect.GenericArrayType
import java.lang.reflect.GenericDeclaration

class SignalCooker {

    fun getSignalBetween(
        startTime: Long,
        endTime: Long = System.currentTimeMillis(),
        signalCookedData: ISignalCookedData
    ) {
        var level: Int
        val db = RoomDB.getDatabase()!!
        val signalList = listOf<GenericDeclaration>().toMutableList()
        for (signal in signalList) {
            level = setCellularLevel(10)
        }
    }

    private fun setCellularLevel(strength: Int): Int {
        return when {
            strength >= -1 -> 0
            strength>=-75 -> 3
            strength>=-85 -> 2
            else -> 0
        }
    }

    private fun setWifiLevel(strength: Int): Int {
        return when {
            strength > -50 -> 3
            strength > -60 -> 2
            strength > -70 -> 1
            else -> 0
        }
    }
}