package com.example.androidDeviceDetails.base

import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod

abstract class SubCooker {
    abstract fun <T> aggregate(time: TimePeriod, callback: ICookingDone<T>)
    
}