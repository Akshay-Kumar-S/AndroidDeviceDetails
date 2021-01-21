package com.example.androidDeviceDetails.models.database

import androidx.room.*

@Entity(foreignKeys = [ForeignKey(entity = AppInfoRaw::class, parentColumns = ["uid"], childColumns =["uid"] )])
data class AppPermissionsInfo(
    @PrimaryKey(autoGenerate = true) val rowId: Int,
    @ColumnInfo val uid: Long,
   // @ColumnInfo(name = "packageName ") var packageName: String,
    @ColumnInfo(name = "permission") var permission: String,
    @ColumnInfo(name = "allowed") var allowed: Boolean,
)

@Dao
interface AppPermissionDao {
    @Query("SELECT * FROM AppPermissionsInfo")
    fun getAll(): List<AppPermissionsInfo>

    @Query("SELECT * FROM AppPermissionsInfo WHERE permission =(:permission)")
    fun getAppPermissions(permission: String): List<AppPermissionsInfo>
}