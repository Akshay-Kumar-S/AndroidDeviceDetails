package com.example.androidDeviceDetails.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.androidDeviceDetails.services.AppService

class OnBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED)
            Intent(context, AppService::class.java).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context.startForegroundService(it)
                else context.startService(it)
            }
    }

}