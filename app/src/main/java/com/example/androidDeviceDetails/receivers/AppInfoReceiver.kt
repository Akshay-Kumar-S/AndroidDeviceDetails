package com.example.androidDeviceDetails.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.utils.AppInfoCollectionHelper

class AppInfoReceiver : BaseCollector() {

    object broadcastReceiver : BroadcastReceiver() {
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

    init {
        start()
    }

    override fun start() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        filter.addDataScheme("package")
        DeviceDetailsApplication.instance.registerReceiver(broadcastReceiver, filter)
    }

    override fun collect() {
    }

    override fun stop() {
        DeviceDetailsApplication.instance.unregisterReceiver(broadcastReceiver)
    }

}

