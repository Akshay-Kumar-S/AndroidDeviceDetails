package com.example.androidDeviceDetails.models.appInfo

/**
 * To record to events of an app
 *
 * [ENROLL] - Given to all the apps that exists on the device during first install of this app
 * [INSTALLED] - When an app is installed for the first time
 * [UPDATED] - When app is updated
 * [UNINSTALLED] - When app is uninstalled
 * [ALL] - Used for filtering events
 */
enum class EventType {
    ENROLL,
    INSTALLED,
    UPDATED,
    UNINSTALLED,
    ALL
}