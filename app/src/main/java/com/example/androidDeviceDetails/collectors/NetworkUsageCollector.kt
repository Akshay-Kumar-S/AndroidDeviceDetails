package com.example.androidDeviceDetails.collectors

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageEntity
import com.example.androidDeviceDetails.models.networkUsageModels.DeviceNetworkUsageEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Implements [BaseCollector].
 *
 * A time based collector which collects the network usage of individual apps
 * and the whole device.
 *
 */
@RequiresApi(Build.VERSION_CODES.M)
class NetworkUsageCollector(var context: Context) : BaseCollector() {
    private val firstInstallTime =
        context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
    val db = RoomDB.getDatabase()!!
    private lateinit var networkStatsManager: NetworkStatsManager

    /**
     *
     * Calls [updateDeviceNetworkUsageDB] function and [updateAppNetworkDataUsageDB] function.
     */
    override fun collect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkStatsManager =
                context.getSystemService(AppCompatActivity.NETWORK_STATS_SERVICE) as NetworkStatsManager
            updateDeviceNetworkUsageDB()
            updateAppNetworkDataUsageDB()
        }
    }

    /**
     * Collect Network Usage data for each app using [NetworkStatsManager.querySummary]
     * which requires [android.Manifest.permission.PACKAGE_USAGE_STATS],
     * store it as a List<[AppNetworkUsageEntity]>
     * and writes into [RoomDB.appNetworkUsageDao].
     *
     */
    private fun updateAppNetworkDataUsageDB() {
        val networkUsageList = arrayListOf<AppNetworkUsageEntity>()
        val networkStatsWifi = networkStatsManager.querySummary(
            NetworkCapabilities.TRANSPORT_WIFI,
            null, firstInstallTime, System.currentTimeMillis()
        )
        val networkStatsMobileData = networkStatsManager.querySummary(
            NetworkCapabilities.TRANSPORT_CELLULAR,
            null, firstInstallTime, System.currentTimeMillis()
        )
        val bucket = NetworkStats.Bucket()
        Log.d("TAG", "updateAppWifiDataUsageDB: ")
        while (networkStatsWifi.hasNextBucket() or networkStatsMobileData.hasNextBucket()) {
            if (networkStatsWifi.hasNextBucket()) {
                networkStatsWifi.getNextBucket(bucket)
                val packageName = context.packageManager.getNameForUid(bucket.uid)
                if (packageName != null && packageName != "null")
                    if (networkUsageList.none { it.packageName == packageName })
                        networkUsageList.add(appNetworkUsageFactory(bucket, true))
                    else
                        networkUsageList.first {
                            it.packageName == packageName
                        }.apply {
                            receivedDataWifi += bucket.rxBytes
                            transferredDataWifi += bucket.txBytes
                        }
            }
            if (networkStatsMobileData.hasNextBucket()) {
                networkStatsMobileData.getNextBucket(bucket)
                val packageName = context.packageManager.getNameForUid(bucket.uid)
                if (packageName != null && packageName != "null")
                    if (networkUsageList.none { it.packageName == packageName })
                        networkUsageList.add(appNetworkUsageFactory(bucket, false))
                    else
                        networkUsageList.first {
                            it.packageName == packageName
                        }.apply {
                            receivedDataMobile += bucket.rxBytes
                            transferredDataMobile += bucket.txBytes
                        }
            }
        }
        GlobalScope.launch { networkUsageList.forEach { db.appNetworkUsageDao().insertAll(it) } }
    }

    /**
     *  Collect Network Usage data for the device using [NetworkStatsManager.querySummaryForDevice]
     *  which requires [android.Manifest.permission.PACKAGE_USAGE_STATS] permission and
     *  writes into [RoomDB.deviceNetworkUsageDao].
     */
    private fun updateDeviceNetworkUsageDB() {
        var totalWifiDataRx = 0L
        var totalWifiDataTx = 0L
        var totalMobileDataRx = 0L
        var totalMobileDataTx = 0L
        var bucket = networkStatsManager.querySummaryForDevice(
            NetworkCapabilities.TRANSPORT_WIFI,
            null, firstInstallTime, System.currentTimeMillis()
        )
        totalWifiDataRx += bucket.rxBytes
        totalWifiDataTx += bucket.txBytes
        bucket = networkStatsManager.querySummaryForDevice(
            NetworkCapabilities.TRANSPORT_CELLULAR,
            null, firstInstallTime, System.currentTimeMillis()
        )
        totalMobileDataRx += bucket.rxBytes
        totalMobileDataTx += bucket.txBytes
        GlobalScope.launch {
            db.deviceNetworkUsageDao().insertAll(
                DeviceNetworkUsageEntity(
                    System.currentTimeMillis(),
                    totalWifiDataTx, totalMobileDataTx,
                    totalWifiDataRx, totalMobileDataRx
                )
            )
        }
    }

    private fun appNetworkUsageFactory(
        bucket: NetworkStats.Bucket,
        wifiEnable: Boolean = true
    ): AppNetworkUsageEntity {
        val packageName = context.packageManager.getNameForUid(bucket.uid)!!
        val timeNow = System.currentTimeMillis()
        return if (wifiEnable)
            AppNetworkUsageEntity(
                0,
                timeNow.minus(timeNow.rem(60 * 1000)), //To make resolution to minutes
                packageName,
                bucket.txBytes, 0L,
                bucket.rxBytes, 0L
            )
        else
            AppNetworkUsageEntity(
                0,
                timeNow.minus(timeNow.rem(60 * 1000)),
                packageName,
                0L, bucket.txBytes, 0L,
                bucket.rxBytes,
            )

    }
}