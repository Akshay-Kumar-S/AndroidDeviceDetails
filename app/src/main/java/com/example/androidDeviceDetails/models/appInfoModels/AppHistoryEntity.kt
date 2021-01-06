package com.example.androidDeviceDetails.models.appInfoModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
@ForeignKey(entity = AppsEntity::class, parentColumns = ["uid"], childColumns = ["appId"])
data class AppHistoryEntity(
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


