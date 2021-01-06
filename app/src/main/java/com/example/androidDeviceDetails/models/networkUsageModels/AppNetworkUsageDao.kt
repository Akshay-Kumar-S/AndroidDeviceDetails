package com.example.androidDeviceDetails.models.networkUsageModels

import androidx.room.*

@Dao
interface AppNetworkUsageDao {
    @Query("SELECT * FROM AppNetworkUsageEntity")
    fun getAll(): List<AppNetworkUsageEntity>

    @Query("SELECT * FROM AppNetworkUsageEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<AppNetworkUsageEntity>

    @Query("DELETE FROM AppNetworkUsageEntity")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg appNetworkUsageEntity: AppNetworkUsageEntity)

    @Delete
    fun delete(appNetworkUsageEntity: AppNetworkUsageEntity)
}