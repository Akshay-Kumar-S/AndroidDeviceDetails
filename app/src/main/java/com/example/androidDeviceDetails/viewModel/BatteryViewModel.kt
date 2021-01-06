package com.example.androidDeviceDetails.viewModel

import android.content.Context
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry

class BatteryViewModel(private val batteryBinding: ActivityBatteryBinding, val context: Context) :
    BaseViewModel() {

    @Suppress("UNCHECKED_CAST")
    override fun <T> onData(outputList: ArrayList<T>) {
        var totalDrop = 0
        if (outputList.isNotEmpty()) {
            val temp = outputList as ArrayList<BatteryAppEntry>
            for (i in temp) totalDrop += i.drop
            batteryBinding.root.post {
                batteryBinding.batteryListView.adapter =
                    BatteryListAdapter(context, R.layout.battery_tile, temp)
                val totalText = "Total drop is $totalDrop %"
                batteryBinding.total.text = totalText
            }
        } else
            batteryBinding.root.post {
                batteryBinding.batteryListView.adapter =
                    BatteryListAdapter(context, R.layout.battery_tile, arrayListOf())
                batteryBinding.total.text = context.getString(R.string.no_usage_recorded)
            }
    }


}