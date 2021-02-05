package com.example.androidDeviceDetails.interfaces

interface ICookingDone<T> {

    fun onComplete(outputList: ArrayList<T>)
}