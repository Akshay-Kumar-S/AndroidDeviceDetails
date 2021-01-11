package com.example.androidDeviceDetails.adapters

import android.widget.TextView
import com.github.davidmoten.geo.GeoHash

data class LocationViewHolder (
    val address :TextView,
    val geoHash: TextView,
    val count : TextView
)