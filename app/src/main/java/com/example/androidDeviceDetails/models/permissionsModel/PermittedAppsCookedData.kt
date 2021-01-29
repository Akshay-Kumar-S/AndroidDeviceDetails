package com.example.androidDeviceDetails.models.permissionsModel
/**
 * A data class used to populate Permitted Apps UI
 *
 * @param package_name Package name of the app
 * @param apk_title Title of the app
 * @param version_name Version name of the app
 * @param isAllowed Whether the permission is allowed or not
 */
data class PermittedAppsCookedData (
    var package_name:String,
    var apk_title:String,
    var version_name:String,
    var isAllowed: Boolean,
)