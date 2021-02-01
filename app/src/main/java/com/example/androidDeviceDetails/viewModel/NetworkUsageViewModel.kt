package com.example.androidDeviceDetails.viewModel

import android.content.Context
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.NetWorkUsageListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.database.AppNetworkUsageRaw
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.utils.SortBy
import com.example.androidDeviceDetails.utils.Utils
import java.util.*

/**
 * Implements [BaseViewModel]
 */
class NetworkUsageViewModel(
    private val binding: ActivityAppDataBinding, val context: Context
) : BaseViewModel() {
    private var itemList = ArrayList<AppNetworkUsageRaw>()
    private var sortBy = SortBy.WIFI_DESCENDING.ordinal

    /**
     * Overrides [onComplete] in [BaseViewModel]
     *
     * Display provided data on UI.
     *
     * @param outputList List of cooked data.
     */
    override fun <T> onComplete(outputList: ArrayList<T>) {
        if (outputList.isNotEmpty()) {
            itemList =
                outputList.filterIsInstance<AppNetworkUsageRaw>() as ArrayList<AppNetworkUsageRaw>
            binding.root.post {
                binding.noData.isVisible = false
                sort(sortBy)
            }
        } else {
            binding.root.post {
                itemList = arrayListOf()
                binding.noData.isVisible = true
                sort(sortBy)
            }
        }
    }

    override fun sort(type: Int) {
        sortBy = type
        when (type) {
            SortBy.WIFI_DESCENDING.ordinal -> itemList.sortByDescending { it.transferredDataWifi + it.receivedDataWifi }
            SortBy.WIFI_ASCENDING.ordinal -> itemList.sortBy { it.transferredDataWifi + it.receivedDataWifi }
            SortBy.CELLULAR_DESCENDING.ordinal -> itemList.sortByDescending { it.transferredDataMobile + it.receivedDataMobile }
            SortBy.CELLULAR_ASCENDING.ordinal -> itemList.sortBy { it.transferredDataMobile + it.receivedDataMobile }
            SortBy.ALPHABETICAL.ordinal -> itemList.sortBy { Utils.getApplicationLabel(it.packageName) }
            SortBy.REVERSE_ALPHABETICAL.ordinal -> itemList.sortByDescending {
                Utils.getApplicationLabel(it.packageName)
            }
        }
        itemList =
            itemList.filterNot { it.receivedDataWifi + it.receivedDataMobile + it.transferredDataMobile + it.transferredDataWifi == 0L } as ArrayList
        val adapter = NetWorkUsageListAdapter(context, R.layout.appdata_tile, itemList)
        binding.appDataListView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}