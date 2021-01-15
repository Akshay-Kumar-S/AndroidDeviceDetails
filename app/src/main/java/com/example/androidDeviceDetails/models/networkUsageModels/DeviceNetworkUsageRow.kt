package com.example.androidDeviceDetails.models.networkUsageModels

import android.net.NetworkCapabilities
import androidx.room.*
import com.example.androidDeviceDetails.collectors.NetworkUsageCollector
import com.example.androidDeviceDetails.models.RoomDB

/**
 * A data class used by [NetworkUsageCollector] to collect the network usage details of the device and
 * write into the [RoomDB.deviceNetworkUsageDao] .
 * @param timeStamp Time of the record.
 * @param transferredDataWifi Amount of data transferred through WIFI- [NetworkCapabilities.TRANSPORT_WIFI].
 * @param transferredDataMobile Amount of data transferred through Cellular Data- [NetworkCapabilities.TRANSPORT_CELLULAR].
 * @param receivedDataWifi Amount of data received through WIFI- [NetworkCapabilities.TRANSPORT_WIFI].
 * @param receivedDataMobile Amount of data received through Cellular Data- [NetworkCapabilities.TRANSPORT_CELLULAR].
 */
@Entity
data class DeviceNetworkUsageEntity(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "transferredDataWifi") val transferredDataWifi: Long,
    @ColumnInfo(name = "transferredDataMobile") val transferredDataMobile: Long,
    @ColumnInfo(name = "receivedDataWifi ") val receivedDataWifi: Long,
    @ColumnInfo(name = "receivedDataMobile ") val receivedDataMobile: Long
)

@Dao
/**
 * An interface that contains functions to handle database operations.
 */
interface DeviceNetworkUsageDao {
    /**
     * Retrieve all the records from [DeviceNetworkUsageDao]
     * @return A list of [DeviceNetworkUsageEntity].
     */
    @Query("SELECT * FROM DeviceNetworkUsageEntity")
    fun getAll(): List<DeviceNetworkUsageEntity>

    /**
     * Retrieve all the records from [DeviceNetworkUsageDao] between the [startTime] and [endTime]
     * @param startTime Start Time
     * @param endTime End Time
     * @return A list of [DeviceNetworkUsageDao].
     */
    @Query("SELECT * FROM DeviceNetworkUsageEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<DeviceNetworkUsageEntity>

    @Query("DELETE FROM DeviceNetworkUsageEntity")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg deviceNetworkUsageEntity: DeviceNetworkUsageEntity)

    @Delete
    fun delete(deviceNetworkUsageEntity: DeviceNetworkUsageEntity)
}


