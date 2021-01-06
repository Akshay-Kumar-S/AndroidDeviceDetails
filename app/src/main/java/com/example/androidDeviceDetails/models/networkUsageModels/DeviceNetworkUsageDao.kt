package com.example.androidDeviceDetails.models.networkUsageModels

import androidx.room.*

@Dao
interface DeviceNetworkUsageDao {
    @Query("SELECT * FROM DeviceNetworkUsageEntity")
    fun getAll(): List<DeviceNetworkUsageEntity>

    @Query("SELECT * FROM DeviceNetworkUsageEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<DeviceNetworkUsageEntity>

    @Query("DELETE FROM DeviceNetworkUsageEntity")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg deviceNetworkUsageEntity: DeviceNetworkUsageEntity)

    @Delete
    fun delete(deviceNetworkUsageEntity: DeviceNetworkUsageEntity)
}