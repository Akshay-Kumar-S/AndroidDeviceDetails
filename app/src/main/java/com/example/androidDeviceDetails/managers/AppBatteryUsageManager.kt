package com.example.androidDeviceDetails.managers

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ListView
import android.widget.TextView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.models.AppUsageModel
import com.example.androidDeviceDetails.models.BatteryRawModel
import com.example.androidDeviceDetails.models.RoomDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppBatteryUsageManager {
    private var db: RoomDB = RoomDB.getDatabase()!!

    private fun getCombinedList(
        appEventList: List<AppUsageModel>,
        batteryListModel: List<BatteryRawModel>
    ): MutableList<MergedEventData> {

        val mergedList = mutableListOf<MergedEventData>()
        val batteryIterator = batteryListModel.iterator()
        var preBattery = batteryListModel.first()

        for (it in appEventList) {
            while ((it.timeStamp > preBattery.timeStamp || preBattery.health == 0) && batteryIterator.hasNext())
                preBattery = batteryIterator.next()
            mergedList.add(
                MergedEventData(
                    it.timeStamp,
                    preBattery.level,
                    preBattery.plugged,
                    it.packageName,
                )
            )
        }
        return mergedList
    }

    @SuppressLint("SetTextI18n")
    fun cookBatteryData(
        context: Context,
        batteryListView: ListView,
        totalTextView: TextView,
    ) {
        val appEntryList = arrayListOf<AppEntry>()
        GlobalScope.launch {
            val appEventList = db.appUsageInfoDao().getAll()
            val batteryList = db.batteryInfoDao().getAll()
            if (batteryList.isNotEmpty() && appEventList.isNotEmpty()) {
                val mergedList = getCombinedList(appEventList, batteryList)
                var previousData = mergedList.first()
                for (mergedEventData in mergedList) {
                    if (mergedEventData.plugged == 0 && previousData.batteryLevel!! > mergedEventData.batteryLevel!!)
                        if (appEntryList.none { it.packageId == previousData.packageName })
                            appEntryList.add(
                                AppEntry(
                                    previousData.packageName,
                                    previousData.batteryLevel!!.minus(mergedEventData.batteryLevel!!)
                                )
                            )
                        else appEntryList.first { it.packageId == previousData.packageName }.drop +=
                            previousData.batteryLevel!!.minus(mergedEventData.batteryLevel!!)
                    previousData = mergedEventData
                }
                var totalDrop = 0
                for (i in appEntryList) totalDrop += i.drop
                batteryListView.post {
                    batteryListView.adapter =
                        BatteryListAdapter(context, R.layout.battery_tile, appEntryList)
                }
                totalTextView.post { totalTextView.text = "Total drop is $totalDrop %" }
            }
        }
    }
}

data class MergedEventData(
    var timestamp: Long,
    var batteryLevel: Int?,
    var plugged: Int?,
    var packageName: String,
)

data class AppEntry(var packageId: String, var drop: Int = 0)