package com.example.androidDeviceDetails.models.permissionsModel

/**
 * A data class used to populate Permitted Apps UI
 *
 * @param packageName Package name of the app
 * @param apkTitle Title of the app
 * @param versionName Version name of the app
 * @param isAllowed Whether the permission is allowed or not
 */
data class PermittedAppsCookedData(
    var packageName: String,
    var apkTitle: String,
    var versionName: String,
    var isAllowed: Boolean,
)