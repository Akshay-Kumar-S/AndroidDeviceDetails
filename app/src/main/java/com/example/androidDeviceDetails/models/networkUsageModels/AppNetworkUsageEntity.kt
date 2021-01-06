package com.example.androidDeviceDetails.models.networkUsageModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppNetworkUsageEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Int,
    @ColumnInfo(name = "timeStamp") val timeStamp: Long,
    @ColumnInfo(name = "packageName ") val packageName: String,
    @ColumnInfo(name = "transferredDataWifi") var transferredDataWifi: Long,
    @ColumnInfo(name = "transferredDataMobile") var transferredDataMobile: Long,
    @ColumnInfo(name = "receivedDataWifi ") var receivedDataWifi: Long,
    @ColumnInfo(name = "receivedDataMobile ") var receivedDataMobile: Long
)

