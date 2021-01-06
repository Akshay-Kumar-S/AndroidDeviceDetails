package com.example.androidDeviceDetails.models.signalModels

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SignalDao {
    @Query("SELECT * FROM SignalEntity")
    fun getAll(): List<SignalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg signalEntity: SignalEntity)

    @Delete
    fun delete(signalEntity: SignalEntity)

    @Query("SELECT * FROM SignalEntity ORDER BY timeStamp DESC LIMIT 1")
    fun getLastLive(): LiveData<SignalEntity>

    @Query("SELECT * FROM SignalEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<SignalEntity>

    @Query("DELETE FROM SignalEntity")
    fun deleteAll()
}