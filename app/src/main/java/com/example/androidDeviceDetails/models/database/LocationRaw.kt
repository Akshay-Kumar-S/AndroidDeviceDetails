package com.example.androidDeviceDetails.models.database

import androidx.room.*
import com.example.androidDeviceDetails.collectors.LocationCollector

/**
 * A data class used by [LocationCollector] to collect the location usage details of the device and
 * write into the [ILocationDao] .
 * @param id Auto-incrementing Primary Key.
 * @param latitude Latitude value in Double.
 * @param longitude Longitude value in Double.
 * @param timeStamp time of location entry in milliseconds.
 */
@Entity(tableName = "Location_Data")
data class LocationModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val latitude: Double,
    val longitude: Double,
    val timeStamp: Long
)

@Dao
interface ILocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationModel)

    /**
     * Retrieve all the records from [ILocationDao]
     * @return A list of [LocationModel].
     */
    @Query("SELECT * FROM Location_Data")
    fun readAll(): List<LocationModel>

    /**
     * Retrieve all the records from [ILocationDao] between the [startTime] and [endTime].
     * @param startTime Start Time
     * @param endTime End Time
     * @return A list of [LocationModel].
     */
    @Query("SELECT * FROM Location_Data where timeStamp between :startTime and :endTime")
    fun readDataFromDate(startTime: Long, endTime: Long): List<LocationModel>
}