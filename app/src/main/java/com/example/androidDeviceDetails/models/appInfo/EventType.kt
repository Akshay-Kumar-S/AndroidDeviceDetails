package com.example.androidDeviceDetails.models.appInfo

/**
 * To record to events of an app
 *
 * [APP_ENROLL] - Given to all the apps that exists on the device during first install of this app
 * [APP_INSTALLED] - When an app is installed for the first time
 * [APP_UPDATED] - When app is updated
 * [APP_UNINSTALLED] - When app is uninstalled
 * [ALL_EVENTS] - Used for filtering events
 */
enum class EventType {
    APP_ENROLL,
    APP_INSTALLED,
    APP_UPDATED,
    APP_UNINSTALLED,
    ALL_EVENTS
}