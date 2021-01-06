package com.example.androidDeviceDetails.models.signalModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WifiEntity(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "strength") val strength: Int?,
    @ColumnInfo(name = "frequency") val frequency: Int?,
    @ColumnInfo(name = "linkSpeed") val linkSpeed: Int?
)