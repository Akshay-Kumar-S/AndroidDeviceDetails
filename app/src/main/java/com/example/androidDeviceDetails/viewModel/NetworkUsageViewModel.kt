package com.example.androidDeviceDetails.viewModel

import android.content.Context
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.NetWorkUsageListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityAppDataBinding
import com.example.androidDeviceDetails.models.networkUsageModels.AppNetworkUsageEntity
import java.util.*


class NetworkUsageViewModel(
    private val networkUsageBinding: ActivityAppDataBinding,
    val context: Context
) : BaseViewModel() {
    @Suppress("UNCHECKED_CAST")
    override fun <T> onData(outputList: ArrayList<T>) {
        if (outputList.isNotEmpty()) {
            networkUsageBinding.root.post {
                networkUsageBinding.apply {
                    appDataListView.adapter = NetWorkUsageListAdapter(
                        context,
                        R.layout.appdata_tile,
                        outputList as ArrayList<AppNetworkUsageEntity>
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