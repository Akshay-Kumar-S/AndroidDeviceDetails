package com.example.androidDeviceDetails.models.database

import androidx.room.*
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppList

@Entity(foreignKeys = [ForeignKey(entity = AppInfoRaw::class, parentColumns = ["uid"], childColumns =["uid"] )])
data class AppPermissionsInfo(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "allowed_permissions") var allowed_permissions: String,
    @ColumnInfo(name = "denied_permissions") var denied_permissions: String,
)

@Dao
interface AppPermissionDao {
    @Query("SELECT * FROM AppPermissionsInfo")
    fun getAll(): List<AppPermissionsInfo>

//    @Query("SELECT * FROM AppPermissionsInfo WHERE permission =(:permission)")
//    fun getAppPermissions(permission: String): List<AppPermissionsInfo>

    @Query("SELECT package_name,apk_title,version_name,allowed_permissions,denied_permissions FROM AppInfoRaw ,AppPermissionsInfo WHERE AppInfoRaw.uid = AppPermissionsInfo.uid")
    fun getPermittedApps():List<PermittedAppList>

    @Insert
    fun insert(vararg appPermissionsInfo: AppPermissionsInfo)
}