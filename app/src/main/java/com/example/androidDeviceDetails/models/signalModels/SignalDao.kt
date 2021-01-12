package com.example.androidDeviceDetails.models.signalModels

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.androidDeviceDetails.viewModel.SignalViewModel

/**
 * An interface that contains functions to handle database operations.
 */
@Dao
interface SignalDao {

    /**
     * Retrieve all records from [SignalEntity] table.
     * @return List of [SignalEntity].
     */
    @Query("SELECT * FROM SignalEntity")
    fun getAll(): List<SignalEntity>

    /**
     * Insert the [signalEntity] into the table.
     * @param signalEntity record to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg signalEntity: SignalEntity)

    /**
     * Delete the [signalEntity] record from the [SignalEntity] table.
     * @param signalEntity record to be deleted.
     */
    @Delete
    fun delete(signalEntity: SignalEntity)

    /**
     * Returns the last [SignalEntity] from the table,
     * also observed by [SignalViewModel] to update UI on realtime basis.
     * @return [SignalEntity] which is used to update UI in [SignalViewModel].
     */
    @Query("SELECT * FROM SignalEntity ORDER BY timeStamp DESC LIMIT 1")
    fun getLastLive(): LiveData<SignalEntity>

    /**
     * Returns all the [SignalEntity] in the given time frame.
     * @param startTime Start Time.
     * @param endTime End Time.
     * @return List of [SignalEntity].
     */
    @Query("SELECT * FROM SignalEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<SignalEntity>

    /**
     * Delete all the records in the [SignalEntity] table.
     */
    @Query("DELETE FROM SignalEntity")
    fun deleteAll()
}