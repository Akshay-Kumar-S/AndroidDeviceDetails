package com.example.androidDeviceDetails.models.permissionsModel

data class AppPermissionData(
    var permissionName: String,
    var allowedAppList: List<String>,
    var deniedAppList: List<String>
)