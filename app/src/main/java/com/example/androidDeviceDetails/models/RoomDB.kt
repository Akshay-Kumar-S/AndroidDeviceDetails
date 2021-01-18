package com.example.androidDeviceDetails.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.models.appInfoModels.*
import com.example.androidDeviceDetails.models.batteryModels.AppEventDao
import com.example.androidDeviceDetails.models.appInfoModels.AppHistoryRaw
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoDao
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoRaw
import com.example.androidDeviceDetails.models.batteryModels.AppEventRaw
import com.example.androidDeviceDetails.models.batteryModels.BatteryDao
import com.example.androidDeviceDetails.models.batteryModels.BatteryRaw
import com.example.androidDeviceDetails.models.locationModels.ILocationDao
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageDao
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageRaw
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageDao
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageRaw
import com.example.androidDeviceDetails.models.signalModels.SignalDao
import com.example.androidDeviceDetails.models.signalModels.SignalRaw


@Database(
    entities = [AppEventRaw::class, BatteryRaw::class, LocationModel::class, AppInfoRaw::class, AppHistoryRaw::class, SignalRaw::class, AppNetworkUsageRaw::class, DeviceNetworkUsageRaw::class],
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