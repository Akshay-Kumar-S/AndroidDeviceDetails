package com.example.androidDeviceDetails.database

import androidx.room.*
import com.example.androidDeviceDetails.models.appInfo.EventType
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData

/**
 * A data class used to enter details into [AppHistoryDao]
 *
 * @param rowId Autogenerated, can be given as 0
 * @param appId Id corresponding to an app, taken from [AppInfoDao]
 * @param timestamp Timestamp
 * @param eventType One of the event from [EventType]
 * @param versionName App's version name
 * @param previousVersionCode Previously recorded version code of the app
 * @param currentVersionCode Apps current version code
 * @param appSize Size of the app in MB
 * @param appTitle Name of the app
 * @param isSystemApp Whether the app is a system app or not
 */
@Entity
@ForeignKey(entity = AppInfoRaw::class, parentColumns = ["uid"], childColumns = ["appId"])

data class AppHistoryRaw(
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

/**
 * An interface that contains functions to handle database operations
 */
@Dao
interface AppHistoryDao {
    /**
     * Retrieve all the records from [AppHistoryDao]
     * @return List of [AppHistoryRaw]
     */
    @Query("SELECT * FROM AppHistoryRaw")
    fun getAll(): List<AppHistoryRaw>

    /**
     * Returns all the Id's of the apps in the given time interval
     * @param startTime Start time
     * @param endTime End time
     * @return List of app Id's
     */
    @Query("SELECT DISTINCT appId FROM AppHistoryRaw WHERE timestamp BETWEEN (:startTime) AND (:endTime) ")
    fun getIdsBetween(startTime: Long, endTime: Long): List<Int>

    /**
     * Returns the most recent record of the app in the given time interval
     * @param appId App Id from [AppInfoDao]
     * @param startTime Start time
     * @param endTime End time
     * @return Most recent entry of [AppHistoryRaw]
     *
     */
    @Query("SELECT * FROM AppHistoryRaw WHERE appId == (:appId) AND timestamp BETWEEN (:startTime) AND (:endTime) ORDER BY rowId DESC LIMIT 1")
    fun getLatestRecordBetween(appId: Int, startTime: Long, endTime: Long): AppHistoryRaw

    /**
     * Returns the oldest record of the app in the given time interval
     * @param appId App Id from [AppInfoDao]
     * @param startTime Start time
     * @param endTime End time
     * @return Oldest entry of [AppHistoryRaw]
     */
    @Query("SELECT * FROM AppHistoryRaw WHERE appId == (:appId) AND timestamp BETWEEN (:startTime) AND (:endTime) ORDER BY rowId ASC LIMIT 1")
    fun getInitialRecordBetween(appId: Int, startTime: Long, endTime: Long): AppHistoryRaw

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: AppHistoryRaw)

    @Delete
    fun delete(user: AppHistoryRaw)

    @Query("SELECT * FROM AppHistoryRaw GROUP BY appId")
    fun getAllApps(): List<AppHistoryRaw>
}