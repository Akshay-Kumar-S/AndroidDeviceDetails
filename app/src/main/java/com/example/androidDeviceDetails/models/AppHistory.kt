package com.example.androidDeviceDetails.models

import androidx.room.*

@Entity
@ForeignKey(entity = Apps::class, parentColumns = ["uid"], childColumns = ["appId"])
data class AppHistory(
    @PrimaryKey(autoGenerate = true) val rowId: Int,
    @ColumnInfo val appId: Int,
    @ColumnInfo(name = "timestamp") var timestamp: Long,
    @ColumnInfo(name = "event_type") var eventType: Int,
    @ColumnInfo(name = "version_name") var versionName: String,
    @ColumnInfo(name = "previous_version_code") var previousVersionCode: Long,
    @ColumnInfo(name = "current_version_code") var currentVersionCode: Long,
    @ColumnInfo(name = "apk_size") var appSize: Long,
    @ColumnInfo(name = "apk_title") var appTitle: String,
    @ColumnInfo(name = "is_system_app") var isSystemApp: Boolean
)


@Dao
interface AppHistoryDao {
    @Query("SELECT * FROM AppHistory")
    fun getAll(): List<AppHistory>

    @Query("SELECT DISTINCT appId FROM AppHistory WHERE timestamp BETWEEN (:startTime) AND (:endTime) ")
    fun getIdsBetween(startTime: Long, endTime: Long): List<Int>

    @Query("SELECT * FROM AppHistory WHERE appId == (:appId) AND timestamp BETWEEN (:startTime) AND (:endTime) ORDER BY rowId DESC LIMIT 1")
    fun getLatestRecordBetween(appId: Int, startTime: Long, endTime: Long): AppHistory

    @Query("SELECT * FROM AppHistory WHERE appId == (:appId) AND timestamp BETWEEN (:startTime) AND (:endTime) ORDER BY rowId ASC LIMIT 1")
    fun getInitialRecordBetween(appId: Int, startTime: Long, endTime: Long): AppHistory

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: AppHistory)

    @Delete
    fun delete(user: AppHistory)
}
