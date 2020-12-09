package com.example.androidDeviceDetails.models

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidDeviceDetails.DeviceDetailsApplication


@Database(entities = [AppUsageRaw::class, BatteryRaw::class], version = 1)
abstract class RoomDB : RoomDatabase() {
    abstract fun batteryInfoDao(): BatteryInfoDao
    abstract fun appUsageInfoDao(): AppUsageInfoDao

    companion object {
        private var INSTANCE: RoomDB? = null
        fun getDatabase(): RoomDB? {
            if (INSTANCE == null) {
                synchronized(RoomDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        DeviceDetailsApplication.instance.applicationContext,
                        RoomDB::class.java, "room_db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}