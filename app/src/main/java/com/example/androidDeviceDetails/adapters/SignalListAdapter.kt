package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import java.text.SimpleDateFormat

class SignalListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<SignalEntity>
) : ArrayAdapter<SignalEntity>(_context, resource, items) {

    @SuppressLint("SimpleDateFormat", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")

        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)
        val timeStamp = view.findViewById<TextView>(R.id.timestamp)
        val strength = view.findViewById<TextView>(R.id.strength)
        val general = view.findViewById<TextView>(R.id.attribute)
        val level = view.findViewById<TextView>(R.id.level)

        timeStamp.text = formatter.format(items[position].timeStamp)
        strength.text = items[position].strength.toString()
        general.text = items[position].attribute
        level.text = items[position].level.toString()
        return view
    }
}