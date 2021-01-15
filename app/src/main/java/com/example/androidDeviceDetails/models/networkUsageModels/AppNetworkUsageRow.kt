package com.example.androidDeviceDetails.models.networkUsageModels

import android.net.NetworkCapabilities
import androidx.room.*
import com.example.androidDeviceDetails.collectors.NetworkUsageCollector
import com.example.androidDeviceDetails.cooker.NetworkUsageCooker
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.viewModel.NetworkUsageViewModel

/**
 * A data class used by [NetworkUsageCollector] to collect the network usage details and
 * write into the [RoomDB.appNetworkUsageDao] also used by [NetworkUsageCooker] and [NetworkUsageViewModel].
 * @param rowId unique id for each entry
 * @param timeStamp Time of the record
 * @param packageName Name of the package.
 * @param transferredDataWifi Amount of data transferred through WIFI- [NetworkCapabilities.TRANSPORT_WIFI].
 * @param transferredDataMobile Amount of data transferred through Cellular Data- [NetworkCapabilities.TRANSPORT_CELLULAR].
 * @param receivedDataWifi Amount of data received through WIFI- [NetworkCapabilities.TRANSPORT_WIFI].
 * @param receivedDataMobile Amount of data received through Cellular Data- [NetworkCapabilities.TRANSPORT_CELLULAR].
 */
@Entity
data class AppNetworkUsageEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Int,
    @ColumnInfo(name = "timeStamp") val timeStamp: Long,
    @ColumnInfo(name = "packageName ") val packageName: String,
    @ColumnInfo(name = "transferredDataWifi") var transferredDataWifi: Long,
    @ColumnInfo(name = "transferredDataMobile") var transferredDataMobile: Long,
    @ColumnInfo(name = "receivedDataWifi ") var receivedDataWifi: Long,
    @ColumnInfo(name = "receivedDataMobile ") var receivedDataMobile: Long
)

/**
 * An interface that contains functions to handle database operations.
 */
@Dao
interface AppNetworkUsageDao {
    /**
     * Retrieve all the records from [AppNetworkUsageDao]
     * @return A list of [AppNetworkUsageEntity].
     */
    @Query("SELECT * FROM AppNetworkUsageEntity")
    fun getAll(): List<AppNetworkUsageEntity>

    /**
     * Retrieve all the records from [AppNetworkUsageDao] between the [startTime] and [endTime]
     * @param startTime Start Time
     * @param endTime End Time
     * @return A list of [AppNetworkUsageEntity].
     */
    @Query("SELECT * FROM AppNetworkUsageEntity WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<AppNetworkUsageEntity>

    @Query("DELETE FROM AppNetworkUsageEntity")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg appNetworkUsageEntity: AppNetworkUsageEntity)

    @Insert()
    fun insertList(finalList:List<AppNetworkUsageEntity>)

    @Delete
    fun delete(appNetworkUsageEntity: AppNetworkUsageEntity)
}

