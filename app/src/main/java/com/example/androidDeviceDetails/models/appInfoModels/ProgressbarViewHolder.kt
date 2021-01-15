package com.example.androidDeviceDetails.models.appInfoModels

import android.widget.ProgressBar
import android.widget.TextView

data class ProgressbarViewHolder(
    val updated_progressBar: ProgressBar,
    val enroll_progressbar: ProgressBar,
    val installed_progressBar: ProgressBar,
    val uninstalled_progressbar: ProgressBar,
    val enroll_count: TextView,
    val install_count: TextView,
    val update_count: TextView,
    val uninstall_count: TextView
)