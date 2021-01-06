package com.example.androidDeviceDetails

interface ICookingDone<T> {
    fun onDone(outputList: MutableList<T>)
}