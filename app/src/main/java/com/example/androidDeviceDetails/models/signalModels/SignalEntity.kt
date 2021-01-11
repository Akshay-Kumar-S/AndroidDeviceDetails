package com.example.androidDeviceDetails.models.signalModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidDeviceDetails.receivers.WifiCollector
import com.example.androidDeviceDetails.managers.SignalChangeCollector
import com.example.androidDeviceDetails.cooker.SignalCooker
import com.example.androidDeviceDetails.models.RoomDB

/**
 * A data class used by the [WifiCollector] and [SignalChangeCollector] to record wifi and cellular
 * signal values and save to the [RoomDB.signalDao] and also used by the [SignalCooker]
 * @param timeStamp Time of the record.
 * @param signal To denote if the input signal row belongs to CELLULAR or WIFI
 *
 * 0 for CELLULAR
 *
 * 1 for WIFI.
 * @param strength Signal strength in dBm.
 * @param attribute LinkSpeed in Mbps for WIFI and CellInfo Type for CELLULAR
 * @param level Signal level.
 *
 *  @see [RoomDB]
 *  @see[SignalDao]
 **/
@Entity
data class SignalEntity(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "signal") val signal: Int,
    @ColumnInfo(name = "strength") val strength: Int,
    @ColumnInfo(name = "attribute") val attribute: String,//linkspeed for wifi and type for cellular
    @ColumnInfo(name = "level") val level: Int
)