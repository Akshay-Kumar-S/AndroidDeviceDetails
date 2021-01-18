package com.example.androidDeviceDetails.models.locationModels

import androidx.room.*

@Entity(tableName = "Location_Data")
data class LocationModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val latitude: Double?,
    val longitude: Double?,
    val time: Long
)

@Dao
interface ILocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationModel)

    @Query("SELECT * FROM Location_Data")
    fun readAll(): List<LocationModel>

    @Query("SELECT * FROM Location_Data where time between :startDate and :endDate")
    fun readDataFromDate(startDate: Long, endDate: Long): List<LocationModel>
}