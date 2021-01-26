package com.example.androidDeviceDetails.models.permissionsModel

data class PermittedAppList(
    var package_name:String,
    var apk_title:String,
    var version_name:String,
    var allowed_permissions: String,
    var denied_permissions: String,
)