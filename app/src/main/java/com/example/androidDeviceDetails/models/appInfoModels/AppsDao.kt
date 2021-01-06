package com.example.androidDeviceDetails.models.appInfoModels

import androidx.room.*

@Dao
interface AppsDao {
    @Query("SELECT * FROM AppsEntity")
    fun getAll(): List<AppsEntity>

    @Query("SELECT * FROM AppsEntity WHERE uid IN (:appId)")
    fun getById(appId: Int): AppsEntity

    @Query("Select uid from AppsEntity WHERE package_name=(:pName)")
    fun getIdByName(pName: String?): Int

    @Query("Select package_name from AppsEntity WHERE uid =(:appId)")
    fun getPackageByID(appId: Int): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg apps: AppsEntity)

    @Delete
    fun delete(user: AppsEntity)
}