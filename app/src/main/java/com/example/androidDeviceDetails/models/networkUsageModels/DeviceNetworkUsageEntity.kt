package com.example.androidDeviceDetails.models.networkUsageModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeviceNetworkUsageEntity(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "transferredDataWifi") val transferredDataWifi: Long,
    @ColumnInfo(name = "transferredDataMobile") val transferredDataMobile: Long,
    @ColumnInfo(name = "receivedDataWifi ") val receivedDataWifi: Long,
    @ColumnInfo(name = "receivedDataMobile ") val receivedDataMobile: Long
)


