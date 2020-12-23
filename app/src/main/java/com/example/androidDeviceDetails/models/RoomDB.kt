package com.example.androidDeviceDetails.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.appInfo.models.AppHistory
import com.example.androidDeviceDetails.appInfo.models.AppHistoryDao
import com.example.androidDeviceDetails.appInfo.models.Apps
import com.example.androidDeviceDetails.appInfo.models.AppsDao


@Database(
    entities = [AppUsageModel::class, BatteryRawModel::class, LocationModel::class, Apps::class, AppHistory::class, WifiRaw::class, CellularRaw::class],
    version = 1
)
abstract class RoomDB : RoomDatabase() {
    abstract fun batteryInfoDao(): BatteryInfoDao
    abstract fun appUsageInfoDao(): AppUsageInfoDao
    abstract fun locationDao(): ILocationDao
    abstract fun appsDao(): AppsDao
    abstract fun appHistoryDao(): AppHistoryDao
    abstract fun wifiDao(): WifiDao
    abstract fun cellularDao(): CellularDao

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