package com.example.androidDeviceDetails.models.appInfoModels

import androidx.room.*

@Dao
interface AppHistoryDao {
    @Query("SELECT * FROM AppHistoryEntity")
    fun getAll(): List<AppHistoryEntity>

    @Query("SELECT DISTINCT appId FROM AppHistoryEntity WHERE timestamp BETWEEN (:startTime) AND (:endTime) ")
    fun getIdsBetween(startTime: Long, endTime: Long): List<Int>

    @Query("SELECT * FROM AppHistoryEntity WHERE appId == (:appId) AND timestamp BETWEEN (:startTime) AND (:endTime) ORDER BY rowId DESC LIMIT 1")
    fun getLatestRecordBetween(appId: Int, startTime: Long, endTime: Long): AppHistoryEntity

    @Query("SELECT * FROM AppHistoryEntity WHERE appId == (:appId) AND timestamp BETWEEN (:startTime) AND (:endTime) ORDER BY rowId ASC LIMIT 1")
    fun getInitialRecordBetween(appId: Int, startTime: Long, endTime: Long): AppHistoryEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: AppHistoryEntity)

    @Delete
    fun delete(user: AppHistoryEntity)
}