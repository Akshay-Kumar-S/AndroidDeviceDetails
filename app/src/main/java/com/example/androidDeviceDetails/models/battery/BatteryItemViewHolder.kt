package com.example.androidDeviceDetails.models.battery

import android.widget.ImageView
import android.widget.TextView


import com.example.androidDeviceDetails.adapters.BatteryListAdapter

/**
 * A data class used by the list view adaptor for displaying in the [BatteryListAdapter]
 */
data class BatteryAppEntry(var packageId: String, var drop: Int = 0)

data class BatteryItemViewHolder(
    var appNameView: TextView,
    var dropTextView: TextView,
    var appIconView: ImageView,
)