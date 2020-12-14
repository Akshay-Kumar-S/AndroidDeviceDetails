package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.AppInfoCookedData
import com.example.androidDeviceDetails.utils.Utils

class AppInfoListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: ArrayList<AppInfoCookedData>
): ArrayAdapter<AppInfoCookedData>(_context, resource, items){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)

        val appNameView = view.findViewById<TextView>(R.id.appName)
        val versionCodeTextView = view.findViewById<TextView>(R.id.dropText)
        val eventTypeTextView = view.findViewById<TextView>(R.id.eventType)
        val appIconView = view.findViewById<ImageView>(R.id.appIcon)

        appNameView.text = Utils.getApplicationLabel(items[position].appName)
        versionCodeTextView.text = "Version Code : " + items[position].versionCode.toString()
        eventTypeTextView.text = "Event " + items[position].eventType.toString()
        appIconView.setImageDrawable(Utils.getApplicationIcon(items[position].appName))

        return view
    }
}