package com.example.androidDeviceDetails.viewModel

import android.content.Context
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.NetWorkUsageListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageRaw
import java.util.*

/**
 * Implements [BaseViewModel]
 */
class NetworkUsageViewModel(
    private val networkUsageBinding: ActivityAppDataBinding,
    val context: Context
) : BaseViewModel() {
    /**
     * Overrides [onData] in [BaseViewModel]
     *
     * Display provided data on UI.
     *
     * @param outputList List of cooked data.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> onDone(outputList: ArrayList<T>) {
        if (outputList.isNotEmpty()) {
            networkUsageBinding.root.post {
                networkUsageBinding.apply {
                    appDataListView.adapter = NetWorkUsageListAdapter(
                        context,
                        R.layout.appdata_tile,
                        outputList as ArrayList<AppNetworkUsageRaw>
                    )
                    noData.isVisible = false
                }
            }
        } else {
            networkUsageBinding.root.post {
                networkUsageBinding.apply {
                    appDataListView.adapter = NetWorkUsageListAdapter(
                        context,
                        R.layout.appdata_tile,
                        arrayListOf()
                    )
                    noData.isVisible = true
                }
            }
        }

    }
}