package com.example.androidDeviceDetails.models.appInfo.appType

import android.graphics.drawable.Drawable

data class AppTypeModel(
    var appIcon : Drawable?,
    var appTitle : String,
    var packageName : String,
    var versionCode : String,
    var versionName : String,
    var packageSize : String,
    var installTime : String,
    var updateTime : String
)
