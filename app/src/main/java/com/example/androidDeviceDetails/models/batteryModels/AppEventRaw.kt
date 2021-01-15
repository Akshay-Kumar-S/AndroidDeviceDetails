package com.example.androidDeviceDetails.models.batteryModels

import androidx.room.*
import com.example.androidDeviceDetails.collectors.AppEventCollector
import com.example.androidDeviceDetails.cooker.BatteryCooker
import com.example.androidDeviceDetails.models.RoomDB

/**
 * A data class used by the [AppEventCollector] to record a battery event and
 * save to the [RoomDB.appEventDao] and also used by the [BatteryCooker]
 * @param timeStamp Time of the record.
 * @param packageName Name of the package.
 *
 * @see [RoomDB]
 * @see [AppEventDao]
 **/
@Entity
data class AppEventRaw(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "packageName ") val packageName: String,
)

/**
 * An interface that contains functions to handle database operations
 */
@Dao
interface AppEventDao {

    /**
     * Retrieve all the records from [AppEventDao]
     * @return List of [AppEventRaw]
     */
    @Query("SELECT * FROM AppEventRaw")
    fun getAll(): List<AppEventRaw>

    /**
     * Returns all the [AppEventRaw] in the given time frame
     * @param startTime Start time
     * @param endTime End time
     * @return List of [AppEventRaw]
     */
    @Query("SELECT * FROM AppEventRaw WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<AppEventRaw>

    @Query("DELETE FROM AppEventRaw")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg appEventRaw: AppEventRaw)

    @Delete
    fun delete(appEventRaw: AppEventRaw)
}
