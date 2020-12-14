package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.managers.AppEntry
import com.example.androidDeviceDetails.utils.Utils

class BatteryListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: ArrayList<AppEntry>
): ArrayAdapter<AppEntry>(_context, resource, items){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)

        val appNameView = view.findViewById<TextView>(R.id.appName)
        val dropTextView = view.findViewById<TextView>(R.id.dropText)
        val appIconView = view.findViewById<ImageView>(R.id.appIcon)

        appNameView.text = Utils.getApplicationLabel(items[position].packageId)
        dropTextView.text = "Total drop is " + items[position].drop.toString()
        appIconView.setImageDrawable(Utils.getApplicationIcon(items[position].packageId))

        return view
    }
}