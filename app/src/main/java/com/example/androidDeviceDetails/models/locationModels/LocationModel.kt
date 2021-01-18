package com.example.androidDeviceDetails.models.locationModels

import androidx.room.*

@Entity(tableName = "Location_Data")
data class LocationModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val latitude: Double?,
    val longitude: Double?,
    val geoHash: String?,
    val time: Long
)

data class test(
    val latitude: Double,
    val longitude: Double
)

@Dao
interface ILocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationModel)

    @Query("SELECT * FROM Location_Data")
    fun readAll(): List<LocationModel>

    @Query("SELECT geoHash, count(geoHash) AS count FROM Location_Data GROUP BY geoHash")
    fun countHash(): List<CountModel>

    @Query("SELECT * FROM Location_Data where time between :startDate and :endDate")
    fun readDataFromDate(startDate: Long, endDate: Long): List<LocationModel>

    @Query("SELECT distinct latitude,longitude From location_data")
    fun selectDistinct(): List<test>
}