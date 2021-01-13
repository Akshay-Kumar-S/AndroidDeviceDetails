package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.MainActivityAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityMainBinding
import com.example.androidDeviceDetails.models.CardItem
import com.example.androidDeviceDetails.models.MainActivityCookedData
import java.util.*

class MainActivityViewModel(
    private val binding: ActivityMainBinding,
    val context: Context
) : BaseViewModel() {
    private var mainActivityModel = MainActivityCookedData(null, -1, null)
    override fun <T> onDone(outputList: ArrayList<T>) {
        val finalList = outputList.filterIsInstance<MainActivityCookedData>()
        if (outputList.isNotEmpty()) {
            val firstElement = finalList.first()
            when {
                firstElement.appInfo!= null -> mainActivityModel.appInfo = firstElement.appInfo
                firstElement.totalDrop != -1L -> mainActivityModel.totalDrop=firstElement.totalDrop
                firstElement.deviceNetworkUsage != null -> mainActivityModel.deviceNetworkUsage=firstElement.deviceNetworkUsage
            }
        }
    }
    fun updateUI(){
        val icons = intArrayOf(
            R.drawable.app_info,
            R.drawable.battery,
            R.drawable.route,
            R.drawable.database,
            R.drawable.wifi,
        )
        val iconsName = arrayOf(
            "App Info",
            "Battery Usage",
            "Location",
            "Network Usage",
            "Signal Data",
        )
        val recyclerView = binding.root.findViewById<View>(R.id.recycler_view) as RecyclerView
        val arrayList = arrayListOf<CardItem>()

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.itemAnimator = DefaultItemAnimator()

        for (i in icons.indices) {
            val itemModel = CardItem()
            itemModel.image = icons[i]
            itemModel.title = iconsName[i]

            //add in array list
            arrayList.add(itemModel)
        }

        val adapter = MainActivityAdapter(context, arrayList)
        recyclerView.adapter = adapter
    }
}