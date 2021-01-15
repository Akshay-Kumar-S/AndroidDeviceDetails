package com.example.androidDeviceDetails.models.batteryModels

import android.os.BatteryManager
import androidx.room.*
import com.example.androidDeviceDetails.collectors.BatteryCollector
import com.example.androidDeviceDetails.cooker.BatteryCooker
import com.example.androidDeviceDetails.models.RoomDB

/**
 * A data class used by the [BatteryCollector] to record a battery event and
 * save to the [RoomDB.batteryDao] and also used by the [BatteryCooker]
 * @param timeStamp Time of the record.
 * @param level Battery level.
 * @param plugged one of [BatteryManager.BATTERY_PLUGGED_AC],
 * [BatteryManager.BATTERY_PLUGGED_USB],
 * [BatteryManager.BATTERY_PLUGGED_WIRELESS],0 : if Device is on battery.
 * @param temp Battery temperature.
 * @param health one of
 * [BatteryManager.BATTERY_HEALTH_COLD],
 * [BatteryManager.BATTERY_HEALTH_DEAD],
 * [BatteryManager.BATTERY_HEALTH_GOOD],
 * [BatteryManager.BATTERY_HEALTH_OVERHEAT],
 * [BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE],
 * [BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE],
 * [BatteryManager.BATTERY_HEALTH_UNKNOWN]
 * @param estimatedCapacity Battery capacity in mAh
 * @param estimatedAccuracy Currently it is the same as [level]
 *
 *  @see [RoomDB]
 *  @see[BatteryDao]
 **/
@Entity
data class BatteryRaw(
    @PrimaryKey val timeStamp: Long,
    @ColumnInfo(name = "level") val level: Int?,
    @ColumnInfo(name = "plugged") val plugged: Int?,
    @ColumnInfo(name = "temp") val temp: Int?,
    @ColumnInfo(name = "health") val health: Int?,
    @ColumnInfo(name = "estimatedCapacity") val estimatedCapacity: Int,
    @ColumnInfo(name = "estimatedAccuracy") val estimatedAccuracy: Int?
)

/**
 * An interface that contains functions to handle database operations
 */
@Dao
interface BatteryDao {

    /**
     * Retrieve all the records from [BatteryDao]
     * @return List of [BatteryRaw]
     */
    @Query("SELECT * FROM BatteryRaw")
    fun getAll(): List<BatteryRaw>

    /**
     * Returns all the [BatteryRaw] in the given time frame
     * @param startTime Start time
     * @param endTime End time
     * @return List of [BatteryRaw]
     */
    @Query("SELECT * FROM BatteryRaw WHERE timeStamp BETWEEN (:startTime) AND (:endTime)")
    fun getAllBetween(startTime: Long, endTime: Long): List<BatteryRaw>

    @Query("DELETE FROM BatteryRaw")
    fun deleteAll()

    @Insert
    fun insert(vararg batteryRaw: BatteryRaw)

    @Insert
    fun insertAll(batteryRawList: List<BatteryRaw>)

    @Delete
    fun delete(batteryRaw: BatteryRaw)
}

