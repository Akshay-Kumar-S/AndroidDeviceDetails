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
import com.example.androidDeviceDetails.models.batteryModels.AppEventRaw
import com.example.androidDeviceDetails.models.batteryModels.BatteryDao
import com.example.androidDeviceDetails.models.batteryModels.BatteryRaw
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageDao
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageRaw
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageDao
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageRaw


@Database(
    entities = [AppEventRaw::class, BatteryRaw::class, LocationModel::class, AppsEntity::class, AppHistoryEntity::class, WifiRaw::class, CellularRaw::class, AppNetworkUsageRaw::class, DeviceNetworkUsageRaw::class],
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