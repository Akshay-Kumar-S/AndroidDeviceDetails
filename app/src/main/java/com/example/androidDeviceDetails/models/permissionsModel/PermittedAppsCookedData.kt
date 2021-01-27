package com.example.androidDeviceDetails.models.permissionsModel

data class PermittedAppsCookedData (
    var package_name:String,
    var apk_title:String,
    var version_name:String,
    var isAllowed: Boolean,
)