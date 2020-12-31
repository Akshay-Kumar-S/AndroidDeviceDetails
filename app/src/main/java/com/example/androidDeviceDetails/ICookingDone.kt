package com.example.androidDeviceDetails

interface ICookingDone<T> {
    fun onNoData()
    fun onData(list: MutableList<T>)
}