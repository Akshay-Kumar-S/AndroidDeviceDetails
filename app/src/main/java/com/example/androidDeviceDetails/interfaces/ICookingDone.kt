package com.example.androidDeviceDetails.interfaces

interface ICookingDone<T> {
    fun onDone(outputList: ArrayList<T>)
}