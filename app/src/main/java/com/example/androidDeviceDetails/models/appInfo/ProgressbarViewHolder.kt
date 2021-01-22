package com.example.androidDeviceDetails.models.appInfo

import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

data class ProgressbarViewHolder(
    val stats1 : LinearLayout,
    val stats4 : LinearLayout,
    val updated_progressBar: ProgressBar,
    val enroll_progressbar: ProgressBar,
    val installed_progressBar: ProgressBar,
    val uninstalled_progressbar: ProgressBar,
    val stats2Text : TextView,
    val stats3Text : TextView,
    val enroll_count: TextView,
    val install_count: TextView,
    val update_count: TextView,
    val uninstall_count: TextView
)