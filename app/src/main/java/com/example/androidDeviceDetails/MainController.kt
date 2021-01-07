package com.example.androidDeviceDetails

import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.androidDeviceDetails.services.AppService
import com.example.androidDeviceDetails.utils.Utils

class MainController {

    fun toggleService(context: Context) {
        if (!Utils.isMyServiceRunning(AppService::class.java, context))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(Intent(context, AppService::class.java))
            else context.startService(Intent(context, AppService::class.java))
        else
            context.stopService(Intent(context, AppService::class.java))
    }
}