package com.example.androidDeviceDetails.models

import androidx.room.*

@Entity(tableName = "Location_Data")
data class LocationModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val latitude: Double?,
    val longitude: Double?,
    val geoHash: String?,
    val time: String?
)

@Dao
interface ILocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationModel)

    @Query("SELECT * FROM Location_Data")
    fun readAll(): List<LocationModel>

    @Query("SELECT geoHash, count(geoHash) AS count FROM Location_Data GROUP BY geoHash")
    fun countHash(): List<CountModel>
}