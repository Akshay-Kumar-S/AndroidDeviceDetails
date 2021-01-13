package com.example.androidDeviceDetails.cooker

import com.example.androidDeviceDetails.base.BaseCooker
import com.example.androidDeviceDetails.interfaces.ICookingDone
import com.example.androidDeviceDetails.models.TimePeriod

class MainActivityCooker:BaseCooker() {
    override fun <T> cook(time: TimePeriod, callback: ICookingDone<T>) {
        TODO("Not yet implemented")
    }
}