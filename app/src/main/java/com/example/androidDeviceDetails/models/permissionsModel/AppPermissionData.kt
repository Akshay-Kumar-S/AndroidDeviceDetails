package com.example.androidDeviceDetails.models.permissionsModel

data class AppPermissionData(
    var permission_name: String,
    var version_name: String,
    var allowed_list: String,
    var denied_list: String
)