package com.example.androidDeviceDetails.viewModel

import android.content.Context
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.cooker.BatteryCooker
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry

class BatteryViewModel(private val binding: ActivityBatteryBinding, val context: Context) :
    BaseViewModel() {

    /**
     * This method is called once the [BatteryCooker] finishes cooking.
     * This updates the UI with the [BatteryListAdapter]
     */
    override fun <T> onDone(outputList: ArrayList<T>) {
        if (outputList.isNotEmpty()) {
            val temp = outputList.filterIsInstance<BatteryAppEntry>() as ArrayList<BatteryAppEntry>
            val totalDrop = temp.sumOf { it.drop }
            val adapter = BatteryListAdapter(context, R.layout.battery_tile, temp)
            binding.root.post {
                binding.listView.adapter = adapter
                val totalText = "Total drop is $totalDrop %"
                binding.total.text = totalText
            }
        } else
            binding.root.post {
                binding.listView.adapter =
                    BatteryListAdapter(context, R.layout.battery_tile, arrayListOf())
                binding.total.text = context.getString(R.string.no_usage_recorded)
            }
    }
}