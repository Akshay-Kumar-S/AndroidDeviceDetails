package com.example.androidDeviceDetails.models.database

import androidx.room.*
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppList

/**
 * A data class used to enter details into [AppPermissionDao]
 *
 * @param uid Primary key, refers uid from [AppInfoRaw]
 * @param allowed_permissions All allowed permissions
 * @param denied_permissions All denied permissions
 */
@Entity(foreignKeys = [ForeignKey(entity = AppInfoRaw::class, parentColumns = ["uid"], childColumns =["uid"] )])
data class AppPermissionsInfo(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "allowed_permissions") var allowed_permissions: String,
    @ColumnInfo(name = "denied_permissions") var denied_permissions: String,
)

/**
 * An interface that contains functions to handle database operations
 */
@Dao
interface AppPermissionDao {
    /**
     * Retrieve all the records from [AppPermissionDao]
     * @return A list of [AppPermissionsInfo].
     */
    @Query("SELECT * FROM AppPermissionsInfo")
    fun getAll(): List<AppPermissionsInfo>

    /**
     * Retrieves package_name,apk_title,version_name,allowed_permissions and denied_permissions from [AppPermissionDao] and [AppInfoDao]
     * @return A list of [PermittedAppList].
     */
    @Query("SELECT package_name,apk_title,version_name,allowed_permissions,denied_permissions FROM AppInfoRaw ,AppPermissionsInfo WHERE AppInfoRaw.uid = AppPermissionsInfo.uid")
    fun getPermittedApps():List<PermittedAppList>

    @Query("SELECT allowed_permissions from AppPermissionsInfo")
    fun getAllPermissions():List<String>

    @Insert
    fun insert(vararg appPermissionsInfo: AppPermissionsInfo)
}