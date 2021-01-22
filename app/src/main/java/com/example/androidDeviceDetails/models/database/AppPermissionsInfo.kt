package com.example.androidDeviceDetails.models.database

import androidx.room.*

@Entity(foreignKeys = [ForeignKey(entity = AppInfoRaw::class, parentColumns = ["uid"], childColumns =["uid"] )])
data class AppPermissionsInfo(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "permission") var permission: String,
)

data class PermittedAppList(
    @ColumnInfo(name="package_name") var package_name:String,
    @ColumnInfo(name = "apk_title") var apk_title:String,
    @ColumnInfo(name = "version_name") var version_name:String,
    @ColumnInfo(name = "permission") var permission: String,
)

@Dao
interface AppPermissionDao {
    @Query("SELECT * FROM AppPermissionsInfo")
    fun getAll(): List<AppPermissionsInfo>

    @Query("SELECT * FROM AppPermissionsInfo WHERE permission =(:permission)")
    fun getAppPermissions(permission: String): List<AppPermissionsInfo>

    @Query("SELECT package_name,apk_title,version_name,permission FROM AppInfoRaw ,AppPermissionsInfo WHERE AppInfoRaw.uid = AppPermissionsInfo.uid")
    fun getPermittedApps():List<PermittedAppList>

    @Insert
    fun insert(vararg appPermissionsInfo: AppPermissionsInfo)
}