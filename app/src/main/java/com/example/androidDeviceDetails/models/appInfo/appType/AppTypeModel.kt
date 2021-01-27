package com.example.androidDeviceDetails.models.appInfo.appType

import android.graphics.drawable.Drawable

data class AppTypeModel(
    var appIcon : Drawable? = null,
    var appTitle : String = "No Data",
    var packageName : String = "No Data",
    var versionCode : String = "No Data",
    var versionName : String = "No Data",
    var packageSize : String = "No Data",
    var installTime : String = "No Data",
    var updateTime : String = "No Data"
)
