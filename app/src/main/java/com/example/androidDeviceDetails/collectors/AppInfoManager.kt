package com.example.androidDeviceDetails.collectors

import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData

object AppInfoManager {

    var appList = arrayListOf<AppInfoCookedData>()

    /**
     * Calculates and assigns the height of the list view
     *
     * @param [listView] App list
     * @param [size] Number of elements in app list
     */
    fun justifyListViewHeightBasedOnChildren(listView: ListView, size: Int) {
        val adapter: ListAdapter = listView.adapter ?: return
        val vg: ViewGroup = listView
        val totalHeight: Int
        val listItem: View = adapter.getView(0, null, vg)
        listItem.measure(0, 0)
        totalHeight = listItem.measuredHeight * size
        val par: ViewGroup.LayoutParams = listView.layoutParams
        par.height = totalHeight + listView.dividerHeight * (adapter.count - 1)
        listView.layoutParams = par
        listView.requestLayout()
    }

}