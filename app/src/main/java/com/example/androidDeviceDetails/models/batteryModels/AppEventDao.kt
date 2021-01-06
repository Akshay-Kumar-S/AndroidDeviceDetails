package com.example.androidDeviceDetails.models.batteryModels

import androidx.room.*

@Dao
interface AppEventDao {
    @Query("SELECT * FROM AppEventEntity")
    fun getAll(): List<AppEventEntity>

    @Query("SELECT * FROM AppEventEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<AppEventEntity>

    @Query("DELETE FROM AppEventEntity")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg appEventEntity: AppEventEntity)

    @Delete
    fun delete(appEventEntity: AppEventEntity)
}