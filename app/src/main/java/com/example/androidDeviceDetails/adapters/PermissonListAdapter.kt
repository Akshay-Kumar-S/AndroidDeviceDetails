package com.example.androidDeviceDetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.signalModels.SignalRaw

class PermissonListAdapter(private var _context: Context, private var resource: Int, private var items: List<String>)
    : ArrayAdapter<String>(_context, resource, items) {

    val layoutInflater = LayoutInflater.from(_context)
    val view = layoutInflater.inflate(resource, null)
    val permissionName = view.findViewById<TextView>(R.id.permisson_type)
}