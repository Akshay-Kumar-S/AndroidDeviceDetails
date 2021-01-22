package com.example.androidDeviceDetails.models.database

import androidx.room.*

@Entity(foreignKeys = [ForeignKey(entity = AppInfoRaw::class, parentColumns = ["uid"], childColumns =["uid"] )])
//@Entity
data class AppPermissionsInfo(
   // @PrimaryKey(autoGenerate = true) val rowId: Int,
    @PrimaryKey val uid: Int,
   // @ColumnInfo(name = "packageName ") var packageName: String,
    @ColumnInfo(name = "permission") var permission: String,
   // @ColumnInfo(name = "allowed") var allowed: Boolean,
)

@Dao
interface AppPermissionDao {
    @Query("SELECT * FROM AppPermissionsInfo")
    fun getAll(): List<AppPermissionsInfo>

    @Query("SELECT * FROM AppPermissionsInfo WHERE permission =(:permission)")
    fun getAppPermissions(permission: String): List<AppPermissionsInfo>

    @Insert
    fun insert(vararg appPermissionsInfo: AppPermissionsInfo)
}