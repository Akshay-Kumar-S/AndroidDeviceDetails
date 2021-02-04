package com.example.androidDeviceDetails.models.database

import androidx.room.*
import com.example.androidDeviceDetails.database.AppInfoRaw
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppListData

/**
 * A data class used to enter details into [AppPermissionDao]
 *
 * @param uid Primary key, refers uid from [AppInfoRaw]
 * @param allowed_permissions All allowed permissions
 * @param denied_permissions All denied permissions
 */
@Entity(foreignKeys = [ForeignKey(entity = AppInfoRaw::class, parentColumns = ["uid"], childColumns =["uid"] )])
data class AppPermissionsRaw(
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
     * @return A list of [AppPermissionsRaw].
     */
    @Query("SELECT * FROM AppPermissionsRaw")
    fun getAll(): List<AppPermissionsRaw>

    /**
     * Retrieves package_name,apk_title,version_name,allowed_permissions and denied_permissions from [AppPermissionDao] and [AppInfoDao]
     * @return A list of [PermittedAppListData].
     */
    @Query("SELECT package_name,apk_title,version_name,allowed_permissions,denied_permissions FROM AppInfoRaw ,AppPermissionsRaw WHERE AppInfoRaw.uid = AppPermissionsRaw.uid")
    fun getPermittedApps():List<PermittedAppListData>

    @Insert
    fun insert(vararg appPermissionsInfo: AppPermissionsRaw)
}