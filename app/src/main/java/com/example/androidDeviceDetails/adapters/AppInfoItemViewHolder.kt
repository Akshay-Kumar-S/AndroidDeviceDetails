package com.example.androidDeviceDetails.adapters

import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

data class AppInfoItemViewHolder(
    var appNameView: TextView,
    var versionCodeTextView : TextView,
    var eventTypeTextView : TextView,
    var appIconView :ImageView,
    var eventBadge : ImageView,
    var uninstallButton : ImageButton
)