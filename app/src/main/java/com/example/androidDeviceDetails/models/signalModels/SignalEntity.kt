package com.example.androidDeviceDetails.models.signalModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SignalEntity(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "signal") val signal: Int,
    @ColumnInfo(name = "strength") val strength: Int,
    @ColumnInfo(name = "attribute") val attribute: String,//linkspeed for wifi and type for cellular
    @ColumnInfo(name = "level") val level: Int
)