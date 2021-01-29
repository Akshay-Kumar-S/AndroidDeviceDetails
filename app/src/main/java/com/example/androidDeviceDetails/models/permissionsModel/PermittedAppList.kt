package com.example.androidDeviceDetails.models.permissionsModel

/**
 * A data class used to populate Permissions App UI
 *
 * @param package_name Package name of the app
 * @param apk_title Title of the app
 * @param version_name Version name of the app
 * @param allowed_permissions String of allowed permissions
 * @param denied_permissions String of denied permissions
 */
data class PermittedAppList(
    var package_name:String,
    var apk_title:String,
    var version_name:String,
    var allowed_permissions: String,
    var denied_permissions: String,
)