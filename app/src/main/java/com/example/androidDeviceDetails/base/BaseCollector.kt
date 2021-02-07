package com.example.androidDeviceDetails.base

abstract class BaseCollector {

    open fun start() {}
    open fun collect() {}
    open fun stop() {}
}