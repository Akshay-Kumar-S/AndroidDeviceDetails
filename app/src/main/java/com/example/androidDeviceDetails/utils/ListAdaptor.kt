package com.example.androidDeviceDetails.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.WifiRaw
import java.text.SimpleDateFormat

class ListAdaptor(
    private var _context: Context,
    private var resource: Int,
    private var items: List<CellularRaw>
): ArrayAdapter<CellularRaw>(_context, resource, items){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var formatter=SimpleDateFormat("dd/MM/yyyy HH:mm")


        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)
        val timeStamp = view.findViewById<TextView>(R.id.textViewTile1)
        val strength = view.findViewById<TextView>(R.id.textViewTile2)
        val level = view.findViewById<TextView>(R.id.textViewTile3)
        timeStamp.text = "      "+formatter.format(items[position].timeStamp);
        strength.text =  items[position].strength.toString()
        level.text=items[position].type.toString()
        return view
    }
}