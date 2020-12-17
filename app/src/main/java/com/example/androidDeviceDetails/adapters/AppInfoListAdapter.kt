package com.example.androidDeviceDetails.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.AppInfoCookedData
import com.example.androidDeviceDetails.utils.EventType
import com.example.androidDeviceDetails.utils.Utils

class AppInfoListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<AppInfoCookedData>
): ArrayAdapter<AppInfoCookedData>(_context, resource, items){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)

        val appNameView = view.findViewById<TextView>(R.id.appName)
        val versionCodeTextView = view.findViewById<TextView>(R.id.appVersionCode)
        val eventTypeTextView = view.findViewById<TextView>(R.id.appEvent)
        val appIconView = view.findViewById<ImageView>(R.id.appIcon)
        val eventBadge = view.findViewById<ImageView>(R.id.event_badge)

        appNameView.text = items[position].appName
        versionCodeTextView.text = "Version Code : " + items[position].versionCode.toString()
        eventTypeTextView.text = " | Event : " + items[position].eventType.toString()
        appIconView.setImageDrawable(Utils.getApplicationIcon(items[position].packageName))
        val color = when (items[position].eventType.ordinal) {
            0 -> R.color.teal_700
            1 -> R.color.purple_500
            2 -> R.color.pink
            3 -> R.color.mat_yellow
            else -> R.color.teal_700
        }
        eventBadge.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.MULTIPLY)
        if(items[position].eventType.ordinal == EventType.APP_ENROLL.ordinal)
        {
            eventBadge.isVisible = false
        }
        return view
    }
}