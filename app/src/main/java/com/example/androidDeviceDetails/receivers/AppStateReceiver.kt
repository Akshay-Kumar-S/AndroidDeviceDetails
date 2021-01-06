package com.example.androidDeviceDetails.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseEventCollector
import com.example.androidDeviceDetails.utils.AppInfoCollectionHelper

class AppStateReceiver : BaseEventCollector, BroadcastReceiver() {

    init {
        registerReceiver()
    }

    override fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        filter.addDataScheme("package")
        DeviceDetailsApplication.instance.registerReceiver(this, filter)
    }

    override fun unregisterReceiver() {
        DeviceDetailsApplication.instance.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val packageName = intent.data?.schemeSpecificPart ?: "not found"

        if (action == Intent.ACTION_PACKAGE_ADDED) {
            if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false))
                AppInfoCollectionHelper.appInstalled(context, packageName)
            else
                AppInfoCollectionHelper.appUpgraded(context, packageName)
        }

        if (action == Intent.ACTION_PACKAGE_FULLY_REMOVED) {
            AppInfoCollectionHelper.appUninstalled(context, packageName)
        }
    }

}

