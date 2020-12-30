package com.example.androidDeviceDetails.utils

import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.WifiRaw

object SignalDbHelper {
    fun writeToWifiDB(wifiRaw:WifiRaw,db: RoomDB){
        db.wifiDao().insertAll(wifiRaw)
    }
    fun writeToCellularDB(cellularRaw:CellularRaw,db: RoomDB){
        db.cellularDao().insertAll(cellularRaw)
    }
}