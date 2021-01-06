package com.example.androidDeviceDetails.models.appInfoModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppsEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "version_name") var versionName: String,
    @ColumnInfo(name = "current_version_code") var currentVersionCode: Long,
    @ColumnInfo(name = "apk_size") var appSize: Long,
    @ColumnInfo(name = "apk_title") var appTitle: String,
    @ColumnInfo(name = "is_system_app") var isSystemApp: Boolean
)


