package com.example.androidDeviceDetails.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class WifiRaw(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "strength") val strength: Int?,
    @ColumnInfo(name = "level") val level: Int?,
    @ColumnInfo(name = "frequency") val frequency: Int?,
    @ColumnInfo(name = "linkSpeed") val linkSpeed: Int?
)

@Dao
interface WifiDao {
    @Query("SELECT * FROM WifiRaw")
    fun getAll(): List<WifiRaw>

    @Insert
    fun insertAll(vararg wifiRaw: WifiRaw)

    @Delete
    fun delete(wifiRaw: WifiRaw)

    @Query("SELECT * FROM wifiraw ORDER BY timeStamp DESC LIMIT 1")
    fun getLastLive(): LiveData<WifiRaw>

    @Query("SELECT * FROM wifiraw WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<WifiRaw>

    @Query("DELETE FROM WifiRaw")
    fun deleteAll()
}