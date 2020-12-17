package com.example.androidDeviceDetails.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class CellularRaw(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "strength") val strength: Int?,
    @ColumnInfo(name = "level") val level: Int?,
    @ColumnInfo(name = "asuLevel") val asuLevel: Int?
)

@Dao
interface CellularDao {
    @Query("SELECT * FROM CellularRaw")
    fun getAll(): List<CellularRaw>

    @Insert
    fun insertAll(vararg cellularRaw: CellularRaw)

    @Delete
    fun delete(cellularRaw: CellularRaw)

    @Query("SELECT * FROM cellularraw ORDER BY timeStamp DESC LIMIT 1")
    fun getLastLive(): LiveData<CellularRaw>

    @Query("SELECT * FROM cellularraw WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<CellularRaw>

    @Query("DELETE FROM CellularRaw")
    fun deleteAll()
}