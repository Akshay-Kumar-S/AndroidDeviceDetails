package com.example.androidDeviceDetails.managers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.androidDeviceDetails.utils.Utils

object AppInfoManager {

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

    fun deleteApp(view: View, packageManager: PackageManager, context: Context) {
        val packageName = view.tag as String
        if (Utils.isPackageInstalled(packageName, packageManager)) {
            val packageURI = Uri.parse("package:${packageName}")
            val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
            context.startActivity(uninstallIntent)
        } else
            Toast.makeText(
                context,
                "App is currently uninstalled",
                Toast.LENGTH_SHORT
            ).show()
    }
}