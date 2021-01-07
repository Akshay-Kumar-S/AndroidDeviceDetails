package com.example.androidDeviceDetails.viewModel

import android.content.Context
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.cooker.BatteryCooker
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry

class BatteryViewModel(private val batteryBinding: ActivityBatteryBinding, val context: Context) :
    BaseViewModel() {

    /**
     * This method is called once the [BatteryCooker] finishes cooking.
     * This updates the UI with the [BatteryListAdapter]
     */
    override fun <T> onDone(outputList: ArrayList<T>) {
        var totalDrop = 0
        if (outputList.isNotEmpty()) {
            val temp = outputList.filterIsInstance<BatteryAppEntry>()
            for (i in temp) totalDrop += i.drop
            batteryBinding.root.post {
                batteryBinding.batteryListView.adapter =
                    BatteryListAdapter(
                        context,
                        R.layout.battery_tile,
                        temp as ArrayList<BatteryAppEntry>
                    )
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

    override fun display(filter: Int) {
        TODO("Not yet implemented")
    }


}