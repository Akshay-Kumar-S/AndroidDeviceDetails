package com.example.androidDeviceDetails.database

import androidx.room.*

/**
 * A data class used to enter details into [AppInfoDao]
 *
 * @param uid Autogenerated, can be given as 0
 * @param packageName Package name of the app
 * @param versionName Version name of the app
 * @param currentVersionCode Current version code of the app
 * @param appSize Size of the app in MB
 * @param appTitle Name of the app
 * @param isSystemApp Whether the app is a system app or not
 */
@Entity
data class AppInfoRaw(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "version_name") var versionName: String,
    @ColumnInfo(name = "current_version_code") var currentVersionCode: Long,
    @ColumnInfo(name = "apk_size") var appSize: Long,
    @ColumnInfo(name = "apk_title") var appTitle: String,
    @ColumnInfo(name = "is_system_app") var isSystemApp: Boolean
)

/**
 * An interface that contains functions to handle database operations
 */
@Dao
interface AppInfoDao {

    /**
     * Retrieve all the records from [AppInfoDao]
     * @return List of [AppInfoRaw]
     */
    @Query("SELECT * FROM AppInfoRaw where current_version_code != 0")
    fun getAll(): List<AppInfoRaw>

    /**
     * Returns [AppInfoRaw] corresponding to [appId]
     * @param appId Id of the app in [AppInfoDao]
     * @return [AppInfoRaw] with the given [appId]
     */
    @Query("SELECT * FROM AppInfoRaw WHERE uid IN (:appId)")
    fun getById(appId: Int): AppInfoRaw

    /**
     * Returns [AppInfoRaw.uid] corresponding to [pName]
     * @param pName Name of the app
     * @return [AppInfoRaw] with the given [pName]
     */
    @Query("Select uid from AppInfoRaw WHERE package_name=(:pName)")
    fun getIdByName(pName: String?): Int

    /**
     * Returns Package name corresponding to [appId]
     * @param appId Id of the app in [AppInfoDao]
     * @return package name of the app with the given [appId]
     */
    @Query("Select package_name from AppInfoRaw WHERE uid =(:appId)")
    fun getPackageByID(appId: Int): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg apps: AppInfoRaw)

    @Delete
    fun delete(user: AppInfoRaw)
}