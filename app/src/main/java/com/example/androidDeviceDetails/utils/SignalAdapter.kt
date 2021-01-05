package com.example.androidDeviceDetails.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.SignalRaw
import java.text.SimpleDateFormat

class SignalAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<SignalRaw>
) : ArrayAdapter<SignalRaw>(_context, resource, items) {

    @SuppressLint("SimpleDateFormat")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")

        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)
        val timeStamp = view.findViewById<TextView>(R.id.timestamp)
        val strength = view.findViewById<TextView>(R.id.strength)
        val general = view.findViewById<TextView>(R.id.general)
        val level = view.findViewById<TextView>(R.id.level)

        timeStamp.text = formatter.format(items[position].timeStamp)
        strength.text = items[position].strength.toString()
        general.text = items[position].general.toString()
        level.text = items[position].level.toString()
        return view
    }
}