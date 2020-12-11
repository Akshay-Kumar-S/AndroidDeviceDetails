package com.example.androidDeviceDetails.models

import androidx.room.*

@Entity
data class Apps(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "package_name") val packageName: String?,

    )


@Dao
interface AppsDao {
    @Query("SELECT * FROM Apps")
    fun getAll(): List<Apps>

    @Query("SELECT * FROM Apps WHERE uid IN (:appId)")
    fun getById(appId: Int): Apps

    @Query("Select uid from Apps WHERE package_name=(:pName)")
    fun getIdByName(pName: String?): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg apps: Apps)

    @Delete
    fun delete(user: Apps)
}