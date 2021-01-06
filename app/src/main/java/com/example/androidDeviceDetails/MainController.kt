package com.example.androidDeviceDetails

import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.androidDeviceDetails.services.CollectorService
import com.example.androidDeviceDetails.utils.Utils

class MainController {

    fun toggleService(context: Context) {
        if (!Utils.isMyServiceRunning(CollectorService::class.java, context))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(Intent(context, CollectorService::class.java))
            else context.startService(Intent(context, CollectorService::class.java))
        else
            context.stopService(Intent(context, CollectorService::class.java))
    }
}