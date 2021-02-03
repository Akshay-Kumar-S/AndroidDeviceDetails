package com.example.androidDeviceDetails.models.permissionsModel

/**
 * A data class used to populate Permissions App UI
 *
 * @param packageName Package name of the app
 * @param apkTitle Title of the app
 * @param versionName Version name of the app
 * @param allowedPermissions String of allowed permissions
 * @param deniedPermissions String of denied permissions
 */
data class PermittedAppList(
    var packageName: String,
    var apkTitle: String,
    var versionName: String,
    var allowedPermissions: String,
    var deniedPermissions: String,
)