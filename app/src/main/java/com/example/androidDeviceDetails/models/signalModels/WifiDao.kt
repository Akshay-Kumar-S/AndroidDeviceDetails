package com.example.androidDeviceDetails.models.signalModels

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WifiDao {
    @Query("SELECT * FROM WifiEntity")
    fun getAll(): List<WifiEntity>

    @Insert
    fun insertAll(vararg wifiEntity: WifiEntity)

    @Delete
    fun delete(wifiEntity: WifiEntity)

    @Query("SELECT * FROM WifiEntity ORDER BY timeStamp DESC LIMIT 1")
    fun getLastLive(): LiveData<WifiEntity>

    @Query("SELECT * FROM WifiEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<WifiEntity>

    @Query("DELETE FROM WifiEntity")
    fun deleteAll()
}