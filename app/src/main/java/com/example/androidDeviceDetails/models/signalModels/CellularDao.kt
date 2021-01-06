package com.example.androidDeviceDetails.models.signalModels

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CellularDao {
    @Query("SELECT * FROM CellularEntity")
    fun getAll(): List<CellularEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cellularEntity: CellularEntity)

    @Delete
    fun delete(cellularEntity: CellularEntity)

    @Query("SELECT * FROM CellularEntity ORDER BY timeStamp DESC LIMIT 1")
    fun getLastLive(): LiveData<CellularEntity>

    @Query("SELECT * FROM CellularEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<CellularEntity>

    @Query("DELETE FROM CellularEntity")
    fun deleteAll()
}