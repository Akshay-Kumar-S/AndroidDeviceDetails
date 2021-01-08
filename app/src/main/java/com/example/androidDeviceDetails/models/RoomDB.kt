package com.example.androidDeviceDetails.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.models.appInfoModels.AppHistoryDao
import com.example.androidDeviceDetails.models.appInfoModels.AppHistoryEntity
import com.example.androidDeviceDetails.models.appInfoModels.AppsDao
import com.example.androidDeviceDetails.models.appInfoModels.AppsEntity
import com.example.androidDeviceDetails.models.batteryModels.AppEventDao
import com.example.androidDeviceDetails.models.batteryModels.AppEventEntity
import com.example.androidDeviceDetails.models.batteryModels.BatteryDao
import com.example.androidDeviceDetails.models.batteryModels.BatteryEntity
import com.example.androidDeviceDetails.models.locationModels.ILocationDao
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageDao
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageEntity
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageDao
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageEntity

@Database(
    entities = [AppEventEntity::class, BatteryEntity::class, LocationModel::class, AppsEntity::class, AppHistoryEntity::class, WifiRaw::class, CellularRaw::class, AppNetworkUsageEntity::class, DeviceNetworkUsageEntity::class],
    version = 1
)
abstract class RoomDB : RoomDatabase() {
    abstract fun batteryDao(): BatteryDao
    abstract fun appEventDao(): AppEventDao
    abstract fun locationDao(): ILocationDao
    abstract fun appsDao(): AppsDao
    abstract fun appHistoryDao(): AppHistoryDao
    abstract fun wifiDao(): WifiDao
    abstract fun cellularDao(): CellularDao
    abstract fun appNetworkUsageDao(): AppNetworkUsageDao
    abstract fun deviceNetworkUsageDao(): DeviceNetworkUsageDao

    companion object {
        private var INSTANCE: RoomDB? = null
        fun getDatabase(context: Context = DeviceDetailsApplication.instance): RoomDB? {
            if (INSTANCE == null) {
                synchronized(RoomDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDB::class.java, "room_db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}