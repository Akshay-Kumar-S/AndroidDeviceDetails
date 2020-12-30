package com.example.androidDeviceDetails.interfaces

import java.lang.reflect.GenericDeclaration

interface ISignalCookedData {
    fun onReceived(signalList:List<GenericDeclaration>)
}