package com.example.androidDeviceDetails.viewModel

import android.content.Context
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.cooker.BatteryCooker
import com.example.androidDeviceDetails.databinding.ActivityBatteryBinding
import com.example.androidDeviceDetails.models.batteryModels.BatteryAppEntry
import com.example.androidDeviceDetails.utils.SortBy
import com.example.androidDeviceDetails.utils.Utils

class BatteryViewModel(private val binding: ActivityBatteryBinding, val context: Context) :
    BaseViewModel() {

    private var itemList = ArrayList<BatteryAppEntry>()
    private var sortBy = SortBy.Descending.ordinal

    /**
     * This method is called once the [BatteryCooker] finishes cooking.
     * This updates the UI with the [BatteryListAdapter]
     */
    override fun <T> onDone(outputList: ArrayList<T>) {
        if (outputList.isNotEmpty()) {
            itemList = outputList.filterIsInstance<BatteryAppEntry>() as ArrayList<BatteryAppEntry>
            val totalDrop = itemList.sumOf { it.drop }
            binding.root.post {
                val totalText = "Total drop is $totalDrop %"
                binding.total.text = totalText
                sort(sortBy)
            }
        } else
            binding.root.post {
                itemList = arrayListOf()
                binding.total.text = context.getString(R.string.no_usage_recorded)
                sort(sortBy)
            }
    }

    override fun sort(type: Int) {
        sortBy = type
        when (type) {
            SortBy.Descending.ordinal -> itemList.sortByDescending { it.drop }
            SortBy.Ascending.ordinal -> itemList.sortBy { it.drop }
            SortBy.Alphabetical.ordinal -> itemList.sortBy { Utils.getApplicationLabel(it.packageId) }
            SortBy.ReverseAlphabetical.ordinal -> itemList.sortByDescending {
                Utils.getApplicationLabel(it.packageId)
            }
        }
        val adapter = BatteryListAdapter(context, R.layout.battery_tile, itemList)
        binding.listView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}