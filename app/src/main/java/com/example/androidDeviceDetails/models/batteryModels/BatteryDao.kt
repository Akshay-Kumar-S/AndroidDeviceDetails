package com.example.androidDeviceDetails.models.batteryModels

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BatteryDao {
    @Query("SELECT * FROM BatteryEntity")
    fun getAll(): List<BatteryEntity>

    @Query("SELECT * FROM BatteryEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<BatteryEntity>

    @Query("DELETE FROM BatteryEntity")
    fun deleteAll()

    @Insert
    fun insertAll(vararg batteryEntity: BatteryEntity)

    @Delete
    fun delete(batteryEntity: BatteryEntity)
}