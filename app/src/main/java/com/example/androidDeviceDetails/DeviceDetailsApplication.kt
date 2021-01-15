package com.example.androidDeviceDetails

import android.app.Application

class DeviceDetailsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: Application
    }
}
