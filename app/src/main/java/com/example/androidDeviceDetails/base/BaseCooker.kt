package com.example.androidDeviceDetails.base

import com.example.androidDeviceDetails.ICookingDone
import com.example.androidDeviceDetails.location.LocationActivity
import com.example.androidDeviceDetails.location.LocationCooker


abstract class BaseCooker {
    abstract fun <T> cook(onCookingDone: ICookingDone<T>, time: Long)

    companion object {
        fun getCooker(type: String): BaseCooker {
            return when (type) {
                LocationActivity.NAME -> LocationCooker()
                else -> LocationCooker()
            }
        }
    }
}