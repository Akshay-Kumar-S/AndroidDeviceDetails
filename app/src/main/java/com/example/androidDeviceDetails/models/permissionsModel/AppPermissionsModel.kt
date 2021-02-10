package com.example.androidDeviceDetails.models.permissionsModel

import android.widget.ImageView
import android.widget.TextView
/**
 * A data class used to populate Permissions App UI
 *
 * @param package_name Package name of the app
 * @param apk_title Title of the app
 * @param version_name Version name of the app
 * @param allowed_permissions String of allowed permissions
 * @param denied_permissions String of denied permissions
 */
data class PermittedAppListData(
    var package_name: String,
    var apk_title: String,
    var version_name: String,
    var allowed_permissions: String,
    var denied_permissions: String,
)
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
data class PermissionsItemViewHolder(
    var permission: TextView,
    var icon: ImageView
)
data class PermissionDetailsItemViewHolder(
    var appName: TextView,
    var versionCode: TextView,
    var appIcon: ImageView
)
data class AppPermissionData(
    var permissionName: String,
    var allowedAppList: List<CookedPermittedList>,
    var deniedAppList: List<CookedPermittedList>
)
data class InstalledPackages(
    var packageName : String,
    var allowedList : ArrayList<String>,
    var deniedList : ArrayList<String>
)
data class CookedPermittedList(
    var package_name: String,
    var apk_title: String,
    var version_name: String,
)