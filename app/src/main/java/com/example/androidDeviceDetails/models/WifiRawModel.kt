package com.example.androidDeviceDetails.models

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
}