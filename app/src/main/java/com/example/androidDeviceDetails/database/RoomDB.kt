package com.example.androidDeviceDetails.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidDeviceDetails.DeviceDetailsApplication

@Database(
    entities = [AppEventRaw::class, BatteryRaw::class, LocationModel::class, AppInfoRaw::class, AppHistoryRaw::class, SignalRaw::class, AppNetworkUsageRaw::class, DeviceNetworkUsageRaw::class, AppPermissionsRaw::class],
    version = 1
)
abstract class RoomDB : RoomDatabase() {

    abstract fun batteryDao(): BatteryDao
    abstract fun appEventDao(): AppEventDao
    abstract fun locationDao(): ILocationDao
    abstract fun appsDao(): AppInfoDao
    abstract fun appHistoryDao(): AppHistoryDao
    abstract fun signalDao(): SignalDao
    abstract fun appNetworkUsageDao(): AppNetworkUsageDao
    abstract fun deviceNetworkUsageDao(): DeviceNetworkUsageDao
    abstract fun appPermissionDao(): AppPermissionDao

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