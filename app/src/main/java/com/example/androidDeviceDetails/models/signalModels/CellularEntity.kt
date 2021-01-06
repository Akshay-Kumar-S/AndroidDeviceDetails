package com.example.androidDeviceDetails.models.signalModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CellularEntity(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "strength") val strength: Int?,
    @ColumnInfo(name = "level") val level: Int?,
    @ColumnInfo(name = "asuLevel") val asuLevel: Int?
)