package com.example.androidDeviceDetails.adapters

import android.widget.ImageView
import android.widget.TextView

data class NetworkUsageItemViewHolder(
    var appNameView: TextView? = null,
    var wifiUsageView: TextView? = null,
    var cellularUsageView: TextView? = null,
    var appIconView: ImageView? = null,
)